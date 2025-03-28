package medical_clinics.physician.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import medical_clinics.clinic.models.Clinic;
import medical_clinics.clinic.services.ClinicService;
import medical_clinics.physician.exceptions.PhysicianAlreadyExistException;
import medical_clinics.physician.exceptions.PhysicianNotFoundException;
import medical_clinics.physician.mapper.PhysicianMapper;
import medical_clinics.physician.model.Physician;
import medical_clinics.physician.repository.PhysicianRepository;
import medical_clinics.schedule.services.DailyScheduleService;
import medical_clinics.shared.exception.PersonalInformationDontMatchException;
import medical_clinics.specialty.model.Specialty;
import medical_clinics.specialty.service.SpecialtyService;
import medical_clinics.user_account.model.Role;
import medical_clinics.web.dto.CreatePatient;
import medical_clinics.web.dto.CreatePhysician;
import medical_clinics.web.dto.NewDaySchedule;
import medical_clinics.web.dto.PhysicianEditRequest;
import medical_clinics.web.dto.events.*;
import medical_clinics.web.dto.response.PhysicianInfo;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PhysicianService {

    private final PhysicianRepository physicianRepository;
    private final DailyScheduleService dailyScheduleService;
    private final ClinicService clinicService;
    private final SpecialtyService specialtyService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public UUID addPhysician ( CreatePhysician physician ) {
        checkPhysicianDataForConflict ( physician );

        eventPublisher.publishEvent ( physician );

        Physician newPhysician = PhysicianMapper.mapFromCreate ( physician );

        Clinic clinic = clinicService.getClinicIdByCityAndAddress (
                physician.getWorkplaceCity ( ), physician.getWorkplaceAddress ( )
        );

        Specialty specialty = specialtyService.getSpecialtyByName ( physician.getSpecialty ( ) );

        newPhysician.setSpecialty ( specialty );
        newPhysician.setWorkplace ( clinic );

        newPhysician = physicianRepository.save ( newPhysician );

        clinicService.addPhysicianSpeciality ( newPhysician );

        PhysicianAccountEvent physicianEvent = new PhysicianAccountEvent (
                newPhysician.getEmail ( )
        );
        eventPublisher.publishEvent ( physicianEvent );
        return newPhysician.getId ( );
    }

    @Transactional
    public void dismissPhysician ( UUID id ) {
        Optional<Physician> physicianById = physicianRepository.findById ( id );

        if ( physicianById.isEmpty ( ) ) {
            throw new PhysicianNotFoundException ( "Physician with provided id dose not exist" );
        }

        Physician physician = physicianById.get ( );

        UUID formerWorkplaceId = physician.getWorkplace ( ).getId ( );
        UUID specialtyId = physician.getSpecialty ( ).getId ( );

        if ( physician.getUserAccount ( ) != null ) {
            notifyUserAccount ( physician.getUserAccount ( ).getId ( ) );
            physician.setUserAccount ( null );
        }

        physician.setWorkplace ( null );
        dailyScheduleService.deletePhysicianFutureSchedules ( physician );

        physicianRepository.save ( physician );

        notifyClinic ( formerWorkplaceId, specialtyId );
    }

    @Transactional
    public void editPhysician ( UUID physicianId, PhysicianEditRequest physicianEdit ) {
        Physician physician = physicianRepository.findById (
                physicianId
        ).orElseThrow ( () ->
                new PhysicianNotFoundException ( "Physician id not found" )
        );

        if ( !physician.getEmail ( ).equals ( physicianEdit.getEmail ( ) ) ) {
            Optional<Physician> findByEmail = physicianRepository.findByEmail ( physicianEdit.getEmail ( ) );

            if ( findByEmail.isPresent ( ) ) {
                throw new PhysicianAlreadyExistException ( "Email already exists" );
            }
        }

        PhysicianChangeEvent physicianChangeEvent = PhysicianChangeEvent.builder ( )
                .firstName ( physicianEdit.getFirstName ( ) )
                .lastName ( physicianEdit.getLastName ( ) )
                .oldEmail ( physician.getEmail ( ) )
                .newEmail ( physicianEdit.getEmail ( ) )
                .build ( );

        eventPublisher.publishEvent ( physicianChangeEvent );

        physician.setEmail ( physicianEdit.getEmail ( ) );
        physician.setFirstName ( physicianEdit.getFirstName ( ) );
        physician.setLastName ( physicianEdit.getLastName ( ) );
        physician.setAbbreviation ( physicianEdit.getAbbreviation ( ) );
        physician.setDescription ( physicianEdit.getDescription ( ) );
        physician.setPictureUrl ( physicianEdit.getPictureUrl ( ) );
        physicianRepository.save ( physician );
    }

    @Transactional
    public void generateSchedule ( UUID physicianAccountId, Collection<NewDaySchedule> dailySchedules ) {
        Optional<Physician> physicianOptional = physicianRepository.findByUserAccount_Id ( physicianAccountId );

        if ( physicianOptional.isEmpty ( ) ) {
            throw new PhysicianNotFoundException ( "Physician not found" );
        }

        Physician physician = physicianOptional.get ( );

        for ( NewDaySchedule newDaySchedule : dailySchedules ) {
            dailyScheduleService.generateDaySchedule ( physician, newDaySchedule );
        }
    }

    public List<PhysicianInfo> getPhysiciansByClinicAndSpeciality ( UUID clinicId, UUID specialityId ) {
        List<Physician> clinicEmployed = physicianRepository.findAllByWorkplace_IdAndSpecialty_Id (
                clinicId, specialityId
        );

        return clinicEmployed.stream ( ).map ( PhysicianMapper::mapToPhysicianInfo ).toList ( );
    }

    public Physician getPhysicianById ( UUID physicianId ) {
        return physicianRepository.findById ( physicianId ).orElseThrow ( () ->
                new PhysicianNotFoundException ( "Physician id not found" )
        );
    }

    public UUID getPhysicianIdByUserAccountId ( UUID physicianAccountId ) {
        return physicianRepository.findByUserAccount_Id ( physicianAccountId ).orElseThrow (
                () -> new PhysicianNotFoundException ( "Physician not found" )
        ).getId ( );
    }

    public PhysicianInfo getPhysicianInfo ( UUID physicianId ) {
        return PhysicianMapper.mapToPhysicianInfo ( getPhysicianById ( physicianId ) );
    }

    @EventListener
    void checkIsEditAccountOnPhysician ( EditedAccountEvent editedAccount ) {
        String newEmail = editedAccount.getNewEmail ( );
        String oldEmail = editedAccount.getOldEmail ( );

        if ( isInformationInConflict ( oldEmail, editedAccount.getFirstName ( ), editedAccount.getLastName ( ) ) ) {
            throw new PersonalInformationDontMatchException (
                    "For editing names in Physician related Account contact your administrator"
            );
        }

        if ( !newEmail.equals ( oldEmail ) ) {
            Optional<Physician> findByNewEmail = physicianRepository.findByEmail ( newEmail );

            if ( findByNewEmail.isPresent ( ) ) {
                throw new PhysicianAlreadyExistException ( "Email already exists" );
            }

            Optional<Physician> findByOldEmail = physicianRepository.findByEmail ( oldEmail );

            if ( findByOldEmail.isPresent ( ) ) {
                Physician physician = findByOldEmail.get ( );

                physician.setEmail ( newEmail );
                physicianRepository.save ( physician );
            }
        }
    }

    @EventListener
    void checkForPhysicianConflict ( CreatePatient patient ) {
        String email = patient.getEmail ( );
        String firstName = patient.getFirstName ( );
        String lastName = patient.getLastName ( );

        boolean physicianDataConflict = isInformationInConflict ( email, firstName, lastName );

        if ( physicianDataConflict ) {
            throw new PersonalInformationDontMatchException (
                    "Patient email match physician with different information"
            );
        }
    }

    @EventListener
    void setAccount ( PatientRoleChangeToPhysician physicianRoleChange ) {
        String email = physicianRoleChange.getUserAccount ( ).getEmail ( );

        Optional<Physician> physicianOptional = physicianRepository.findByEmail ( email );

        if ( physicianOptional.isPresent ( ) ) {
            Physician physician = physicianOptional.get ( );
            physician.setUserAccount ( physicianRoleChange.getUserAccount ( ) );
            physicianRepository.save ( physician );
        }
    }

    @EventListener
    void addPhysicianAccount ( NewUserAccountEvent newUserAccount ) {
        String newAccountEmail = newUserAccount.getUserAccount ( ).getEmail ( );
        String firstName = newUserAccount.getFirstName ( );
        String lastName = newUserAccount.getLastName ( );

        if ( isInformationInConflict ( newAccountEmail, firstName, lastName ) ) {
            throw new PersonalInformationDontMatchException (
                    "Personal information don't match the email"
            );
        }

        Optional<Physician> optionalPhysician = physicianRepository.findByEmail ( newAccountEmail );

        if ( optionalPhysician.isPresent ( ) ) {
            Physician physician = optionalPhysician.get ( );

            physician.setUserAccount ( newUserAccount.getUserAccount ( ) );
            physicianRepository.save ( physician );

            eventPublisher.publishEvent ( new PhysicianAccountEvent ( newAccountEmail ) );
        }
    }

    @EventListener
    void checkIsPhysicianAccount ( DemoteAccountEvent userAccountDemoted ) {
        Optional<Physician> physician = physicianRepository.findByUserAccount_Id (
                userAccountDemoted.getAccountId ( )
        );

        if ( physician.isPresent ( ) && !Role.PHYSICIAN.equals ( userAccountDemoted.getRole ( ) ) ) {
            eventPublisher.publishEvent ( new PhysicianAccountEvent ( physician.get ( ).getEmail ( ) ) );
        }
    }

    private boolean isInformationInConflict ( String email, String firstName, String lastName ) {
        Optional<Physician> optionalPhysician = physicianRepository.findByEmail ( email );

        if ( optionalPhysician.isEmpty ( ) ) {
            return false;
        }

        Physician physician = optionalPhysician.get ( );

        return !physician.getFirstName ( ).equals ( firstName ) || !physician.getLastName ( ).equals ( lastName );
    }

    private void checkPhysicianDataForConflict ( CreatePhysician physician ) {
        Optional<Physician> physicianByIdentNumber =
                physicianRepository.findByIdentificationNumber ( physician.getIdentificationNumber ( ) );

        Optional<Physician> physicianByEmail =
                physicianRepository.findByEmail ( physician.getEmail ( ) );

        if ( physicianByIdentNumber.isPresent ( ) || physicianByEmail.isPresent ( ) ) {
            throw new PhysicianAlreadyExistException (
                    "Physician with same identification code or email already exists exists"
            );
        }
    }

    private void notifyClinic ( UUID workplaceId, UUID specialtyId ) {
        boolean isTheOnlySpecialistInClinic = physicianRepository
                .findAllByWorkplace_IdAndSpecialty_Id ( workplaceId, specialtyId )
                .size ( ) == 1;

        if ( isTheOnlySpecialistInClinic ) {
            eventPublisher.publishEvent ( new NoSpecialistsLeftEvent ( workplaceId, specialtyId ) );
        }
    }

    private void notifyUserAccount ( UUID userAccountId ) {
        if ( userAccountId != null ) {
            eventPublisher.publishEvent ( new DismissedStaffEvent ( userAccountId ) );
        }
    }
}

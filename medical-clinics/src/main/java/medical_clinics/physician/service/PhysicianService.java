package medical_clinics.physician.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import medical_clinics.clinic.models.Clinic;
import medical_clinics.clinic.services.ClinicService;
import medical_clinics.physician.model.Physician;
import medical_clinics.physician.repository.PhysicianRepository;
import medical_clinics.schedule.services.DailyScheduleService;
import medical_clinics.shared.exception.PersonalInformationDontMatchException;
import medical_clinics.shared.exception.PhysicianAlreadyExistException;
import medical_clinics.shared.exception.PhysicianNotFoundException;
import medical_clinics.shared.mappers.PhysicianMapper;
import medical_clinics.specialty.model.Specialty;
import medical_clinics.specialty.service.SpecialtyService;
import medical_clinics.web.dto.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Collection;
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
    public void addPhysician ( CreatePhysician physician ) {
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

        NewPhysicianEvent physicianEvent = new NewPhysicianEvent (
                newPhysician
        );
        eventPublisher.publishEvent ( physicianEvent );
    }

    @Transactional
    public void editPhysician ( PhysicianEditRequest physicianEdit ) {
        Physician physician = physicianRepository.findById (
                physicianEdit.getId ( )
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
    }

    @Transactional
    public void generateSchedule ( UUID physicianAccountId, Collection<DailyScheduleDto> dailySchedules ) {
        Optional<Physician> physicianOptional = physicianRepository.findByUserAccount_Id ( physicianAccountId );

        if ( physicianOptional.isEmpty ( ) ) {
            throw new PhysicianNotFoundException ( "Physician not found" );
        }

        Physician physician = physicianOptional.get ( );

        for ( DailyScheduleDto dailyScheduleDto : dailySchedules ) {
            dailyScheduleService.generateDaySchedule ( physician, dailyScheduleDto );
        }
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

}

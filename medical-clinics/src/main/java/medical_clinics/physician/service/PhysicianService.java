package medical_clinics.physician.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import medical_clinics.clinic.services.ClinicService;
import medical_clinics.patient.service.PatientService;
import medical_clinics.physician.mapper.PhysicianMapper;
import medical_clinics.physician.model.Physician;
import medical_clinics.physician.repository.PhysicianRepository;
import medical_clinics.shared.exception.PersonalInformationDontMatchException;
import medical_clinics.shared.exception.PhysicianAlreadyExistException;
import medical_clinics.shared.exception.PhysicianNotFoundException;
import medical_clinics.specialty.service.SpecialtyService;
import medical_clinics.user_account.model.Role;
import medical_clinics.user_account.model.UserAccount;
import medical_clinics.user_account.service.UserAccountService;
import medical_clinics.web.dto.CreatePhysician;
import medical_clinics.web.dto.PhysicianEditRequest;
import medical_clinics.web.dto.RegisterRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PhysicianService {
    private final PhysicianRepository physicianRepository;
    private final ClinicService clinicService;
    private final SpecialtyService specialtyService;
    private final UserAccountService userAccountService;
    private final PatientService patientService;

    @Transactional
    public void addNew ( CreatePhysician physician ) {
        checkPhysicianDataForConflict ( physician );

        Physician newPhysician = PhysicianMapper.mapFromCreate ( physician );

        Optional<UserAccount> account = userAccountService.getAccountIdByEmail ( physician.getEmail ( ) );

        if ( account.isPresent ( ) ) {
            checkForPatientConflict (
                    physician.getEmail ( ), physician.getFirstName ( ), physician.getLastName ( )
            );
            UserAccount userAccount = account.get ( );
            userAccount.setRole ( Role.PHYSICIAN );
            newPhysician.setUserAccount ( userAccount );
        }

        UUID clinicID = clinicService.getClinicIdByCityAndAddress (
                physician.getWorkplaceCity ( ), physician.getWorkplaceAddress ( )
        );

        UUID specialityID = specialtyService.getIdBySpecialtyName ( physician.getSpecialty ( ) );

        newPhysician.setSpecialty ( specialtyService.addPhysician ( specialityID, newPhysician ) );
        newPhysician.setWorkplace ( clinicService.addPhysician ( clinicID, newPhysician ) );
        physicianRepository.save ( newPhysician );
    }

    public boolean isEmailOfPhysician ( String email ) {
        return physicianRepository.findByEmail ( email ).isPresent ( );
    }

    @Transactional
    public void addPhysicianAccount ( RegisterRequest registerRequest, UserAccount userAccount ) {
        Optional<Physician> physicianOptional = physicianRepository.findByEmail ( registerRequest.getEmail ( ) );

        if ( physicianOptional.isEmpty ( ) ) {
            throw new PhysicianNotFoundException ( "Physician not found" );
        }

        Physician physician = physicianOptional.get ( );

        if ( !physician.getFirstName ( ).equals ( registerRequest.getFirstName ( ) ) ||
                !physician.getLastName ( ).equals ( registerRequest.getLastName ( ) ) ) {
            throw new PersonalInformationDontMatchException (
                    "Personal information don't match the email"
            );
        }

        physician.setUserAccount ( userAccount );
        physicianRepository.save ( physician );
    }

    @Transactional
    public void editPhysician ( UUID id, PhysicianEditRequest physicianEdit ) {
        Physician physician = getById ( id );

        if ( !physician.getEmail ( ).equals ( physicianEdit.getEmail ( ) ) ) {
            Optional<Physician> findByEmail = physicianRepository.findByEmail ( physicianEdit.getEmail ( ) );

            if ( findByEmail.isPresent ( ) ) {
                throw new PhysicianAlreadyExistException ( "Email already exists" );
            }
        }

        if ( patientService.findByEmail ( physician.getEmail ( ) ) ) {
            patientService.editPatient ( physician.getEmail ( ), physicianEdit );
        }

        if ( userAccountService.findByEmail ( physician.getEmail ( ) ) ) {
            userAccountService.changeAccountEmail ( physician.getEmail ( ), physicianEdit.getEmail ( ) );
        }

        physician.setEmail ( physicianEdit.getEmail ( ) );
        physician.setFirstName ( physicianEdit.getFirstName ( ) );
        physician.setLastName ( physicianEdit.getLastName ( ) );
        physician.setAbbreviation ( physicianEdit.getAbbreviation ( ) );
        physician.setDescription ( physicianEdit.getDescription ( ) );
        physician.setPictureUrl ( physicianEdit.getPictureUrl ( ) );
    }

    public boolean isInformationInConflict ( String email, String firstName, String lastName ) {
        Optional<Physician> physicianByEmail = physicianRepository.findByEmail ( email );

        if ( physicianByEmail.isEmpty ( ) ) {
            return false;
        }
        Physician physician = physicianByEmail.get ( );

        return !physician.getFirstName ( ).equals ( firstName ) || !physician.getLastName ( ).equals ( lastName );
    }

    private void checkForPatientConflict ( String email, String firstName, String lastName ) {
        boolean patientDataConflict = patientService.isInformationInConflict (
                email,
                firstName,
                lastName
        );

        if ( patientDataConflict ) {
            throw new PersonalInformationDontMatchException (
                    "Physician email match patient with different information"
            );
        }
    }

    private void checkPhysicianDataForConflict ( CreatePhysician physician ) {
        Optional<Physician> physicianByIdentNumber =
                physicianRepository.findByIdentificationNumber ( physician.getIdentificationNumber ( ) );

        Optional<Physician> physicianByEmail =
                physicianRepository.findByEmail ( physician.getIdentificationNumber ( ) );

        if ( physicianByIdentNumber.isPresent ( ) || physicianByEmail.isPresent ( ) ) {
            throw new PhysicianAlreadyExistException (
                    "Physician with same identification code or email already exists exists"
            );
        }
    }

    private Physician getById ( UUID id ) {
        return physicianRepository.findById ( id ).orElseThrow ( () ->
                new PhysicianNotFoundException ( "Physician id not found" )
        );
    }
}

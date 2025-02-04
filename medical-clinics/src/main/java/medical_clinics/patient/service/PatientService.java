package medical_clinics.patient.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import medical_clinics.patient.mapper.PatientMapper;
import medical_clinics.patient.model.Patient;
import medical_clinics.patient.repository.PatientRepository;
import medical_clinics.physician.service.PhysicianService;
import medical_clinics.shared.exception.PatientAlreadyExistsException;
import medical_clinics.shared.exception.PersonalInformationDontMatchException;
import medical_clinics.shared.exception.PatientNotFoundException;
import medical_clinics.user_account.model.UserAccount;
import medical_clinics.user_account.service.UserAccountService;
import medical_clinics.web.dto.CreateEditPatient;
import medical_clinics.web.dto.PatientInfo;
import medical_clinics.web.dto.PhysicianEditRequest;
import medical_clinics.web.dto.RegisterRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatientService {
    private static final String PATIENT_NOT_FOUND = "Patient with provided id dose not exist";
    private static final String PATIENT_MATCH_OTHER_PERSON = "Patient information match other person data";

    private final PatientRepository patientRepository;
    private final PhysicianService physicianService;
    private final UserAccountService userAccountService;

    public void addPatient ( CreateEditPatient createEditPatient ) {
        Patient patient = PatientMapper.mapFromCreateEditPatient ( createEditPatient );

        if ( checkPatientExist ( patient, patient.getEmail ( ) ) ||
                isCountryAndIdentificationPresent (
                        patient.getCountry ( ),
                        patient.getIdentificationCode ( )
                )
        ) {
            throw new PatientAlreadyExistsException ( "Patient already exists" );
        }

        checkForPhysicianConflict (
                patient.getEmail ( ), patient.getFirstName ( ), patient.getLastName ( )
        );

        patientRepository.save ( patient );
    }

    public PatientInfo getPatientInfoById ( UUID id ) {
        Patient patient = patientRepository.findById ( id ).orElseThrow ( () ->
                new PatientNotFoundException ( PATIENT_NOT_FOUND ) );

        return PatientMapper.mapToPatientInfo ( patient );
    }

    public PatientInfo getPatientInfoByEmail ( String email ) {
        Patient patient = patientRepository.findByEmail ( email ).orElseThrow ( () ->
                new PatientNotFoundException ( PATIENT_NOT_FOUND ) );

        return PatientMapper.mapToPatientInfo ( patient );
    }

    public PatientInfo getPatientInfoByPhone ( String phoneNumber ) {
        Patient patient = patientRepository.findByPhone ( phoneNumber ).orElseThrow ( () ->
                new PatientNotFoundException ( PATIENT_NOT_FOUND ) );

        return PatientMapper.mapToPatientInfo ( patient );
    }

    public PatientInfo getPatientInfoByCountryAndIdentificationCode (
            String country, String identificationCode
    ) {
        Patient patient = patientRepository.findByCountryAndIdentificationCode (
                        country, identificationCode
                )
                .orElseThrow (
                        () -> new PatientNotFoundException ( PATIENT_NOT_FOUND )
                );

        return PatientMapper.mapToPatientInfo ( patient );
    }

    @Transactional
    public void editPatientInfo ( UUID id, CreateEditPatient editRequest ) {
        Patient patient = getPatientByEmail ( editRequest.getEmail ( ) );

        checkForNewInformationConflict ( patient, editRequest );
        checkForPhysicianConflict (
                editRequest.getEmail ( ), editRequest.getFirstName ( ), editRequest.getLastName ( )
        );
        if ( isContactEdited ( patient.getEmail ( ), editRequest.getEmail ( ) ) &&
                        userAccountService.findByEmail ( patient.getEmail ( ) ) ) {

            userAccountService.changeAccountEmail (
                    patient.getEmail ( ), editRequest.getEmail ( )
            );
        }
        patient = PatientMapper.mapFromCreateEditPatient ( editRequest );
        patient.setId ( id );
        patientRepository.save ( patient );
    }

    public void editPatient ( String oldEmail, PhysicianEditRequest physicianEdit ) {
        Patient patient = getPatientByEmail ( oldEmail );

        if ( isEmailInConflict ( patient, physicianEdit.getEmail ( ) ) ) {
            throw new PersonalInformationDontMatchException ( PATIENT_MATCH_OTHER_PERSON );
        }
        patient.setEmail ( physicianEdit.getEmail ( ) );
        patient.setFirstName ( physicianEdit.getFirstName ( ) );
        patient.setLastName ( physicianEdit.getLastName ( ) );

        patientRepository.save ( patient );
    }

    @Transactional
    public void addPatientAccount ( RegisterRequest request, UserAccount userAccount ) {
        Patient patient = PatientMapper.mapFromRegistrationRequest ( request );

        if ( checkPatientExist ( patient, userAccount.getEmail ( ) ) ) {
            Optional<Patient> patientByPhone = patientRepository.findByPhone ( patient.getPhone ( ) );

            patient = patientByPhone.orElseGet ( () ->
                    patientRepository.findByEmail ( userAccount.getEmail ( ) ).get ( ) );

            if ( patient.getPhone ( ) == null ) {
                patient.setPhone ( request.getPhone ( ) );
            }
        }
        patient.setEmail ( userAccount.getEmail ( ) );
        patient.setUserAccount ( userAccount );
        patientRepository.save ( patient );
    }

    public boolean findByEmail ( String email ) {
        return patientRepository.findByEmail ( email ).isPresent ( );
    }

    public boolean isInformationInConflict (
            String email, String firstName, String lastName
    ) {
        Optional<Patient> patientByEmail = patientRepository.findByEmail ( email );

        if ( patientByEmail.isEmpty ( ) ) {
            return false;
        }
        Patient patient = patientByEmail.get ( );

        return !patient.getFirstName ( ).equals ( firstName ) ||
                !patient.getLastName ( ).equals ( lastName );
    }

    private boolean checkPatientExist ( Patient request, String email ) {
        Optional<Patient> patientByEmail = Optional.empty ( );

        if ( email != null && !email.isBlank ( ) ) {
            patientByEmail = patientRepository.findByEmail ( email );
        }

        Optional<Patient> patientByPhone = Optional.empty ( );

        if ( request.getPhone ( ) != null ) {
            patientByPhone = patientRepository.findByPhone ( request.getPhone ( ) );
        }

        if ( patientByEmail.isEmpty ( ) && patientByPhone.isEmpty ( ) ) {
            return false;
        }

        if ( patientByPhone.isPresent ( ) && patientByEmail.isPresent ( ) ) {
            boolean isEmailAndPhoneOnSamePerson = isNamesMatches (
                    patientByEmail.get ( ), patientByPhone.get ( )
            );

            if ( isEmailAndPhoneOnSamePerson ) {
                return isNamesMatches ( patientByEmail.get ( ), request );
            }
        }

        if ( patientByEmail.isEmpty ( ) ) {
            return isNamesMatches ( patientByPhone.get ( ), request );
        }

        return isNamesMatches ( patientByEmail.get ( ), request );
    }

    private boolean isNamesMatches ( Patient patient, Patient fromRegistrationRequest ) {
        if ( !patient.getFirstName ( ).equals ( fromRegistrationRequest.getFirstName ( ) ) ||
                !patient.getLastName ( ).equals ( fromRegistrationRequest.getLastName ( ) ) ) {

            throw new PersonalInformationDontMatchException ( "Phone number or email dose not match with names" );
        }
        return true;
    }

    private boolean isCountryAndIdentificationPresent ( String country, String identification ) {
        return patientRepository.findByCountryAndIdentificationCode ( country, identification ).isPresent ( );
    }

    private void checkForNewInformationConflict ( Patient patient, CreateEditPatient editPatient ) {
        if ( isIdentificationInConflict ( patient, editPatient ) ||
                areContactsInConflict ( patient, editPatient.getEmail ( ), editPatient.getPhone ( ) ) ) {

            throw new PatientAlreadyExistsException ( PATIENT_MATCH_OTHER_PERSON );
        }
    }

    private boolean isIdentificationInConflict ( Patient patient, CreateEditPatient editPatient ) {
        if ( !patient.getIdentificationCode ( ).equals ( editPatient.getIdentificationCode ( ) ) ||
                !patient.getCountry ( ).equals ( editPatient.getCountry ( ) ) ) {

            return isCountryAndIdentificationPresent (
                    editPatient.getCountry ( ), editPatient.getIdentificationCode ( )
            );
        }
        return false;
    }

    private boolean areContactsInConflict ( Patient patient, String newEmail, String newPhone ) {
        return isEmailInConflict ( patient, newEmail ) || isPhoneInConflict ( patient, newPhone );
    }

    private boolean isEmailInConflict ( Patient patient, String newEmail ) {
        if ( newEmail == null ) {
            return false;
        }

        if ( !isContactEdited ( patient.getEmail ( ), newEmail ) ) {
            return false;
        }

        return patientRepository.findByEmail ( newEmail ).isPresent ( );
    }

    private boolean isPhoneInConflict ( Patient patient, String newPhone ) {
        if ( newPhone == null ) {
            return false;
        }

        if ( !isContactEdited ( patient.getPhone ( ), newPhone ) ) {
            return false;
        }

        return patientRepository.findByPhone ( newPhone ).isPresent ( );
    }

    private boolean isContactEdited ( String oldContact, String newContact ) {
        boolean isContactPresent = oldContact != null;

        if ( isContactPresent ) {
            return !oldContact.equals ( newContact );
        }
        return newContact != null;
    }

    private void checkForPhysicianConflict ( String email, String firstName, String lastName ) {
        boolean physicianDataConflict = physicianService.isInformationInConflict (
                email,
                firstName,
                lastName
        );

        if ( physicianDataConflict ) {
            throw new PersonalInformationDontMatchException (
                    "Patient email match physician with different information"
            );
        }
    }

    private Patient getPatientByEmail ( String email ) {
        return patientRepository.findByEmail ( email ).orElseThrow ( () ->
                new PatientNotFoundException ( PATIENT_NOT_FOUND )
        );
    }
}

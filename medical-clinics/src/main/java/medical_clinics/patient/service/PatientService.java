package medical_clinics.patient.service;

import lombok.RequiredArgsConstructor;
import medical_clinics.patient.exceptions.PatientAlreadyExistsException;
import medical_clinics.patient.exceptions.PatientNotFoundException;
import medical_clinics.patient.mapper.PatientMapper;
import medical_clinics.patient.model.Patient;
import medical_clinics.patient.repository.PatientRepository;
import medical_clinics.shared.exception.PersonalInformationDontMatchException;
import medical_clinics.user_account.exceptions.UserAlreadyExistsException;
import medical_clinics.web.dto.CreatePatient;
import medical_clinics.web.dto.CreatePhysician;
import medical_clinics.web.dto.events.EditedAccountEvent;
import medical_clinics.web.dto.events.NewUserAccountEvent;
import medical_clinics.web.dto.events.PhysicianChangeEvent;
import medical_clinics.web.dto.response.PatientInfo;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatientService {
    private static final String PATIENT_NOT_FOUND = "Patient with provided information dose not exist";
    private static final String MATCH_OTHER_PERSON = "Match other person data";

    private final PatientRepository patientRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void addPatient ( CreatePatient createPatient ) {
        Patient patient = PatientMapper.mapFromCreateEditPatient ( createPatient );

        boolean findByPhoneOrEmail = checkPatientExist ( patient );
        boolean findByCountryAndIdentificationCode = isCountryAndIdentificationPresent (
                patient.getCountry ( ),
                patient.getIdentificationCode ( )
        );

        if ( findByPhoneOrEmail || findByCountryAndIdentificationCode ) {
            throw new PatientAlreadyExistsException ( "Patient already exists" );
        }

        eventPublisher.publishEvent ( createPatient );

        patientRepository.save ( patient );
    }

    public Patient getPatientById ( UUID id ) {
        return patientRepository.findById ( id ).orElseThrow ( () ->
                new PatientNotFoundException ( PATIENT_NOT_FOUND )
        );
    }

    public PatientInfo getPatientInfoByEmail ( String email ) {
        Patient patient = patientRepository.findByEmail ( email ).orElseThrow ( () ->
                new PatientNotFoundException ( PATIENT_NOT_FOUND )
        );

        return PatientMapper.mapToPatientInfo ( patient );
    }

    public PatientInfo getPatientInfoByPhone ( String phoneNumber ) {
        Patient patient = patientRepository.findByPhone ( phoneNumber ).orElseThrow ( () ->
                new PatientNotFoundException ( PATIENT_NOT_FOUND )
        );

        return PatientMapper.mapToPatientInfo ( patient );
    }

    public PatientInfo getPatientInfoByUserAccountId ( UUID accountId ) {
        return PatientMapper.mapToPatientInfo ( getPatientByUserAccountId ( accountId ) );
    }

    public Patient getPatientByUserAccountId ( UUID userAccountId ) {
        return patientRepository.findByUserAccount_Id ( userAccountId ).orElseThrow ( () ->
                new PatientNotFoundException ( PATIENT_NOT_FOUND )
        );
    }

    public PatientInfo getPatientInfoByCountryAndIdentificationCode (
            String country, String identificationCode ) {

        Patient patient = patientRepository.findByCountryAndIdentificationCode (
                        country, identificationCode
                )
                .orElseThrow (
                        () -> new PatientNotFoundException ( PATIENT_NOT_FOUND )
                );

        return PatientMapper.mapToPatientInfo ( patient );
    }

    @EventListener
    void editPatientInfo ( EditedAccountEvent editedAccount ) {
        String newEmail = editedAccount.getNewEmail ( );
        String oldEmail = editedAccount.getOldEmail ( );

        Optional<Patient> findByOldEmail = patientRepository.findByEmail ( oldEmail );

        if ( findByOldEmail.isEmpty ( ) ) {
            throw new PatientNotFoundException ( PATIENT_NOT_FOUND );
        }

        Patient patient = findByOldEmail.get ( );

        if ( areContactsInConflict ( patient, newEmail, editedAccount.getPhone ( ) ) ) {
            throw new PersonalInformationDontMatchException ( MATCH_OTHER_PERSON );
        }

        patient.setFirstName ( editedAccount.getFirstName ( ) );
        patient.setLastName ( editedAccount.getLastName ( ) );
        patient.setEmail ( newEmail );
        patient.setPhone ( editedAccount.getPhone ( ) );
        patient.setCountry ( editedAccount.getCountry ( ) );
        patient.setCity ( editedAccount.getCity ( ) );
        patient.setAddress ( editedAccount.getAddress ( ) );

        patientRepository.save ( patient );
    }

    @EventListener
    void editPhysicianWhichIsPatient ( PhysicianChangeEvent physicianChange ) {
        Optional<Patient> patientOptional = patientRepository.findByEmail (
                physicianChange.getOldEmail ( )
        );

        if ( patientOptional.isEmpty ( ) ) {
            return;
        }

        Patient patient = patientOptional.get ( );

        if ( isEmailInConflict ( patient, physicianChange.getNewEmail ( ) ) ) {
            throw new PatientAlreadyExistsException ( MATCH_OTHER_PERSON );
        }

        patient.setEmail ( physicianChange.getNewEmail ( ) );
        patient.setFirstName ( physicianChange.getFirstName ( ) );
        patient.setLastName ( physicianChange.getLastName ( ) );

        patientRepository.save ( patient );
    }

    @EventListener
    void addPatientAccount ( NewUserAccountEvent newUserAccount ) {
        Patient patient = PatientMapper.mapFromNewUserAccount ( newUserAccount );

        if ( checkPatientExist ( patient ) ) {
            Optional<Patient> patientByPhone = patientRepository.findByPhone ( patient.getPhone ( ) );

            if ( patientByPhone.isEmpty ( ) ) {
                patient = patientRepository.findByEmail ( patient.getEmail ( ) ).get ( );

                if ( patient.getUserAccount ( ) != null ) {
                    throw new UserAlreadyExistsException ( "User already has account " + patient.getUserAccount ( ).getEmail ( ) );
                }

                if ( patient.getPhone ( ) == null ) {
                    patient.setPhone ( newUserAccount.getPhoneNumber ( ) );
                }

            } else {
                patient = patientByPhone.get ( );

                if ( patient.getUserAccount ( ) != null ) {
                    throw new UserAlreadyExistsException ( "User already has account " + patient.getUserAccount ( ).getEmail ( ) );
                }

                patient.setEmail ( newUserAccount.getUserAccount ( ).getEmail ( ) );
            }
        }
        patient.setUserAccount ( newUserAccount.getUserAccount ( ) );
        patientRepository.save ( patient );
    }

    @EventListener
    void checkForPatientConflict ( CreatePhysician physician ) {
        String email = physician.getEmail ( );
        String firstName = physician.getFirstName ( );
        String lastName = physician.getLastName ( );
        boolean isPatientDataConflict = isInformationInConflict ( email, firstName, lastName );

        if ( isPatientDataConflict ) {
            throw new PersonalInformationDontMatchException (
                    "Physician email match patient with different information"
            );
        }
    }

    private boolean isInformationInConflict ( String email, String firstName, String lastName ) {
        Optional<Patient> patientByEmail = patientRepository.findByEmail ( email );

        if ( patientByEmail.isEmpty ( ) ) {
            return false;
        }
        Patient patient = patientByEmail.get ( );

        return !patient.getFirstName ( ).equals ( firstName ) ||
                !patient.getLastName ( ).equals ( lastName );
    }

    private boolean checkPatientExist ( Patient request ) {
        PersonalInformationDontMatchException error = new PersonalInformationDontMatchException (
                "Phone number or email dose not match with names"
        );

        String email = request.getEmail ( );
        String phone = request.getPhone ( );

        Optional<Patient> patientByEmail = Optional.empty ( );

        if ( email != null && !email.isBlank ( ) ) {
            patientByEmail = patientRepository.findByEmail ( email );
        }

        Optional<Patient> patientByPhone = Optional.empty ( );

        if ( phone != null && !phone.isBlank ( ) ) {
            patientByPhone = patientRepository.findByPhone ( phone );
        }

        if ( patientByEmail.isEmpty ( ) && patientByPhone.isEmpty ( ) ) {
            return false;
        }

        if ( patientByPhone.isPresent ( ) && patientByEmail.isPresent ( ) ) {
            boolean isEmailAndPhoneOnSamePerson = isNamesMatches (
                    patientByEmail.get ( ), patientByPhone.get ( )
            );

            if ( !isEmailAndPhoneOnSamePerson ) {
                throw error;
            }

            if ( !isNamesMatches ( patientByEmail.get ( ), request ) ) {
                throw error;
            }
            return true;
        }

        if ( patientByEmail.isEmpty ( ) ) {
            if ( isNamesMatches ( patientByPhone.get ( ), request ) ) {
                return true;
            }
            throw error;
        }

        if ( isNamesMatches ( patientByEmail.get ( ), request ) ) {
            return true;
        }
        throw error;
    }

    private boolean isNamesMatches ( Patient patient1, Patient patient2 ) {
        return patient1.getFirstName ( ).equals ( patient2.getFirstName ( ) ) &&
                patient1.getLastName ( ).equals ( patient2.getLastName ( ) );
    }

    private boolean isCountryAndIdentificationPresent ( String country, String identification ) {
        return patientRepository.findByCountryAndIdentificationCode ( country, identification ).isPresent ( );
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
}

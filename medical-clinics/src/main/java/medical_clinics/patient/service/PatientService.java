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

import java.util.*;

@Service
@RequiredArgsConstructor
public class PatientService {
    private static final String PATIENT_NOT_FOUND = "Patient with provided information dose not exist";
    private static final String MATCH_OTHER_PERSON = "Match other person data";

    private final PatientRepository patientRepository;
    private final ApplicationEventPublisher eventPublisher;

    public UUID addPatient ( CreatePatient createPatient ) {
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

        return patientRepository.save ( patient ).getId ( );
    }

    public void updatePatientInfo ( UUID patientId, String country, String identificationCode ) {
        Optional<Patient> patientByIdentificationProvided = patientRepository.findByCountryAndIdentificationCode (
                country, identificationCode
        );

        if ( patientByIdentificationProvided.isPresent ( ) ) {
            throw new PatientAlreadyExistsException ( MATCH_OTHER_PERSON );
        }

        Patient patient = getPatientById ( patientId );

        patient.setCountry ( country );
        patient.setIdentificationCode ( identificationCode );

        patientRepository.save ( patient );
    }

    public PatientInfo getPatientInfoById ( UUID patientId ) {
        return PatientMapper.mapToPatientInfo ( getPatientById ( patientId ) );
    }

    public Patient getPatientById ( UUID id ) {
        return patientRepository.findById ( id ).orElseThrow ( () ->
                new PatientNotFoundException ( PATIENT_NOT_FOUND )
        );
    }

    public PatientInfo getPatientInfoByUserAccountId ( UUID accountId ) {
        return PatientMapper.mapToPatientInfo ( getPatientByUserAccountId ( accountId ) );
    }

    public Patient getPatientByUserAccountId ( UUID userAccountId ) {
        return patientRepository.findByUserAccount_Id ( userAccountId ).orElseThrow ( () ->
                new PatientNotFoundException ( PATIENT_NOT_FOUND )
        );
    }

    public List<PatientInfo> findPatient ( String phoneNumber,
                                           String email,
                                           Map<String, String> countryAndIdentificationCode ) {

        Map<UUID, Patient> patientInfoList = new HashMap<> ( );

        if ( email != null && !email.isBlank ( ) ) {
            patientRepository.findByEmail ( email )
                    .ifPresent ( value -> patientInfoList.put ( value.getId ( ), value ) );
        }

        if ( phoneNumber != null && !phoneNumber.isBlank ( ) ) {

            patientRepository.findByPhone ( phoneNumber )
                    .ifPresent ( value -> patientInfoList.putIfAbsent ( value.getId ( ), value ) );
        }

        if ( countryAndIdentificationCode.containsKey ( "country" ) &&
                countryAndIdentificationCode.containsKey ( "identificationCode" ) ) {

            patientRepository.findByCountryAndIdentificationCode (
                    countryAndIdentificationCode.get ( "country" ),
                    countryAndIdentificationCode.get ( "identificationCode" )

            ).ifPresent ( value -> patientInfoList.putIfAbsent ( value.getId ( ), value ) );
        }

        return patientInfoList.values ( ).stream ( )
                .map ( PatientMapper::mapToPatientInfo )
                .toList ( );
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
                    throw new UserAlreadyExistsException (
                            "User already has account " + patient.getUserAccount ( ).getEmail ( )
                    );
                }

                if ( patient.getPhone ( ) == null ) {
                    patient.setPhone ( newUserAccount.getPhoneNumber ( ) );
                }

            } else {
                patient = patientByPhone.get ( );

                if ( patient.getUserAccount ( ) != null ) {
                    throw new UserAlreadyExistsException (
                            "User already has account " + patient.getUserAccount ( ).getEmail ( )
                    );
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

        if ( isContactRetains ( patient.getEmail ( ), newEmail ) ) {
            return false;
        }

        return patientRepository.findByEmail ( newEmail ).isPresent ( );
    }

    private boolean isPhoneInConflict ( Patient patient, String newPhone ) {
        if ( newPhone == null ) {
            return false;
        }

        if ( isContactRetains ( patient.getPhone ( ), newPhone ) ) {
            return false;
        }

        return patientRepository.findByPhone ( newPhone ).isPresent ( );
    }

    private boolean isContactRetains ( String oldContact, String newContact ) {
        boolean isContactPresent = oldContact != null;

        if ( isContactPresent ) {
            return oldContact.equals ( newContact );
        }
        return newContact == null;
    }
}

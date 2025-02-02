package medical_clinics.patient.service;

import lombok.RequiredArgsConstructor;
import medical_clinics.patient.mapper.PatientMapper;
import medical_clinics.patient.model.Patient;
import medical_clinics.patient.repository.PatientRepository;
import medical_clinics.shared.exception.PatientDontMatchException;
import medical_clinics.user_account.model.UserAccount;
import medical_clinics.web.dto.RegisterRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    public void addPatientAccount ( RegisterRequest request, UserAccount userAccount ) {
        Patient patient = PatientMapper.mapFromRegistrationRequest ( request );

        if ( checkPatientExist ( patient, userAccount.getEmail ( ) ) ) {
            Optional<Patient> patientByPhone = patientRepository.findByPhone ( patient.getPhone ( ) );

            patient = patientByPhone.orElseGet ( () ->
                    patientRepository.findByEmail ( userAccount.getEmail ( ) ).get ( ) );
        }
        patient.setUserAccount ( userAccount );
        patientRepository.save ( patient );
    }

    private boolean checkPatientExist ( Patient fromRegistrationRequest, String email ) {
        Optional<Patient> patientByEmail = patientRepository.findByEmail ( email );
        boolean isPhonePresent = fromRegistrationRequest.getPhone ( ) != null;

        Optional<Patient> patientByPhone = Optional.empty ( );

        if ( isPhonePresent ) {
            patientByPhone = patientRepository.findByPhone ( fromRegistrationRequest.getPhone ( ) );
        }

        if ( patientByEmail.isEmpty ( ) && patientByPhone.isEmpty ( ) ) {
            return false;
        }

        if ( patientByPhone.isPresent ( ) && patientByEmail.isPresent ( ) ) {
            boolean isEmailAndPhoneOnSamePerson = isNamesMatches ( patientByEmail.get ( ), patientByPhone.get ( ) );

            if ( isEmailAndPhoneOnSamePerson ) {
                return isNamesMatches ( patientByEmail.get ( ), fromRegistrationRequest );
            }
        }

        if ( patientByEmail.isEmpty ( ) ) {
            return isNamesMatches ( patientByPhone.get ( ), fromRegistrationRequest );
        }

        return isNamesMatches ( patientByEmail.get ( ), fromRegistrationRequest );
    }

    private boolean isNamesMatches ( Patient patient, Patient fromRegistrationRequest ) {

        if ( !patient.getFirstName ( ).equals ( fromRegistrationRequest.getFirstName ( ) ) ||
                !patient.getLastName ( ).equals ( fromRegistrationRequest.getLastName ( ) ) ) {

            throw new PatientDontMatchException ( "Phone number or email dose not match with names" );
        }
        return true;
    }

}

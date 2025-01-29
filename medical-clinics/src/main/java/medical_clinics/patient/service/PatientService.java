package medical_clinics.patient.service;

import lombok.RequiredArgsConstructor;
import medical_clinics.patient.model.Patient;
import medical_clinics.patient.repository.PatientRepository;
import medical_clinics.user_account.model.UserAccount;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    public Optional<UUID> findByPhone ( String phone ) {
        Optional<Patient> patient = patientRepository.findByPhone ( phone );
        return patient.map ( Patient::getId );
    }

    public void addPatientAccount ( UUID patientId, UserAccount userAccount ) {
        patientRepository.findById ( patientId ).ifPresent ( patient -> {
            patient.setUserAccount ( userAccount );
            patientRepository.save ( patient );
        } );
    }
    public void addPatient ( String firstName, String lastName, UserAccount userAccount ) {
        Patient patient = Patient.builder ( )
                .firstName ( firstName )
                .lastName ( lastName )
                .userAccount ( userAccount )
                .build ( );

        patientRepository.save ( patient );

    }
    public void addPatient ( String firstName, String lastName, String phone, UserAccount userAccount ) {
        Patient patient = Patient.builder ( )
                .firstName ( firstName )
                .lastName ( lastName )
                .phone ( phone )
                .userAccount ( userAccount )
                .build ( );

        patientRepository.save ( patient );

    }
}

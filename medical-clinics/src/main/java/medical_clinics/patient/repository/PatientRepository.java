package medical_clinics.patient.repository;

import medical_clinics.patient.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {
    Optional<Patient> findByPhone ( String phone );

    Optional<Patient> findByEmail ( String email );

    Optional<Patient> findByCountryAndIdentificationCode ( String country, String identificationCode );
}

package medical_clinics.physician.repository;

import medical_clinics.physician.model.Physician;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PhysicianRepository extends JpaRepository<Physician, UUID> {
    Optional<Physician> findByIdentificationNumber ( String identificationNumber );

    Optional<Physician> findByEmail ( String email );

    Optional<Physician> findByUserAccount_Id ( UUID userAccountId );
}

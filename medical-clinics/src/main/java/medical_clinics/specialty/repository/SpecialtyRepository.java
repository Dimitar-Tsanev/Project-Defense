package medical_clinics.specialty.repository;

import medical_clinics.specialty.model.Specialty;
import medical_clinics.specialty.model.SpecialtyName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty, UUID> {
    Optional<Specialty> getByName ( SpecialtyName name );
}

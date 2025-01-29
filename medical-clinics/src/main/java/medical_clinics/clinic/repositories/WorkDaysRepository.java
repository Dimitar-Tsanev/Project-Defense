package medical_clinics.clinic.repositories;

import medical_clinics.clinic.models.WorkDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WorkDaysRepository extends JpaRepository<WorkDay, UUID> {
}

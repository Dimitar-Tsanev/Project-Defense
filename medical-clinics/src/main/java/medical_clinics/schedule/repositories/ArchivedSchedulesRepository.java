package medical_clinics.schedule.repositories;

import medical_clinics.schedule.models.ArchivedSchedules;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ArchivedSchedulesRepository extends JpaRepository<ArchivedSchedules, UUID> {
}

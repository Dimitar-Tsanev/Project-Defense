package medical_clinics.schedule.repositories;

import medical_clinics.schedule.models.DailySchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DailyScheduleRepository extends JpaRepository<DailySchedule, UUID> {
    List<DailySchedule> findAllByPhysicianId ( UUID physicianId );

    Optional<DailySchedule> findByPhysicianIdAndDate ( UUID physicianId, LocalDate date );

    List<DailySchedule> findAllByDateBefore ( LocalDate dateBefore );

    List<DailySchedule> findAllByPhysicianIdAndDateAfter ( UUID id, LocalDate now );
}

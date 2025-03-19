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
    List<DailySchedule> findAllByPhysicianIdOrderByDateAsc ( UUID physicianId );

    Optional<DailySchedule> findByPhysicianIdAndDate ( UUID physicianId, LocalDate date );

    Optional<DailySchedule> findAllByPhysician_UserAccount_IdAndDate ( UUID userAccountId, LocalDate date );

    List<DailySchedule> findAllByDateBefore ( LocalDate dateBefore );

    List<DailySchedule> findAllByPhysician_IdAndDateAfter ( UUID id, LocalDate now );

    List<DailySchedule> findAllByPhysician_UserAccount_IdOrderByDateAsc ( UUID userAccountId );
}

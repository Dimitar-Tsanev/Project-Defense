package medical_clinics.schedule.repositories;

import medical_clinics.schedule.models.Status;
import medical_clinics.schedule.models.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, UUID> {

    List<TimeSlot> findAllByStatusEqualsAndStartTimeBeforeAndDailySchedule_Date ( Status status, LocalTime time, LocalDate date );

    List<TimeSlot> findAllByPatientId ( UUID patientId );
}

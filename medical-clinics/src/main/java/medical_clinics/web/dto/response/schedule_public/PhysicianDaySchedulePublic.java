package medical_clinics.web.dto.response.schedule_public;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter

public class PhysicianDaySchedulePublic {
    private UUID scheduleId;

    private LocalDate date;

    private List<DayAppointmentPublic> schedule;
}

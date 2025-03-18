package medical_clinics.web.dto.response.schedule_private;

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

public class PhysicianDaySchedulePrivate {
    private UUID id;

    private LocalDate date;

    private List<DayAppointmentPrivate> schedule;
}

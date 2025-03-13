package medical_clinics.web.dto.response.apointmet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import medical_clinics.schedule.models.Status;

import java.time.LocalTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter

public class DayAppointmentPublic implements DayAppointment {
    private UUID id;

    private Status status;

    private LocalTime startTime;
}

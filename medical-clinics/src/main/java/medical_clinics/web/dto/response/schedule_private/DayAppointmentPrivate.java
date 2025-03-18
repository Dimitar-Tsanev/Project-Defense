package medical_clinics.web.dto.response.schedule_private;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import medical_clinics.schedule.models.Status;
import medical_clinics.web.dto.response.PatientInfo;

import java.time.LocalTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter

public class DayAppointmentPrivate {
    private UUID id;

    private Status status;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(type = "string", example = "17:00:00")
    private LocalTime startTime;

    private PatientInfo patientInfo;
}

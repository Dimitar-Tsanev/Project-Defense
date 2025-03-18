package medical_clinics.web.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder

public class PatientAppointment {
    private UUID id;

    private LocalDate appointmentDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(type = "string", example = "17:00:00")
    private LocalTime startTime;

    private String physician;

    private String address;
}

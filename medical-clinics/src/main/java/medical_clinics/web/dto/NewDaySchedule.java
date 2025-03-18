package medical_clinics.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder

public class NewDaySchedule {

    @NotNull(message = "{not.blank}")
    @Future(message = "{schedule.date}")
    private LocalDate date;

    @NotNull(message = "{not.blank}")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    @Schema(type = "string", pattern = "HH:mm:ss", example = "10:00:00")
    private LocalTime startTime;

    @NotNull(message = "{not.blank}")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    @Schema(type = "string", pattern = "HH:mm:ss", example = "16:00:00")
    private LocalTime endTime;

    @NotNull(message = "{not.blank}")
    @Schema(type = "integer", pattern = "\\d+", example = "30", maximum = "60", minimum = "15")
    @Min(value = 15, message = "{schedule.interval}")
    @Max(value = 60, message = "{schedule.interval}")
    private Integer timeSlotInterval;
}

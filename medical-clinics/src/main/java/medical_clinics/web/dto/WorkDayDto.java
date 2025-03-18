package medical_clinics.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import medical_clinics.clinic.models.DaysOfWeek;
import medical_clinics.web.validation.day_of_week.DayOfWeekName;

import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter

public class WorkDayDto {

    @NotNull(message = "{not.blank}")
    @DayOfWeekName(message = "{day.of.week}")
    @Schema(type = "enum", example = "thursday", description = "case insensitive", implementation = DaysOfWeek.class)
    private String dayName;

    @NotNull(message = "{not.blank}")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    @Schema(type = "string", pattern = "HH:mm:ss", example = "08:00:00")
    private LocalTime startOfWorkingDay;

    @NotNull(message = "{not.blank}")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    @Schema(type = "string", pattern = "HH:mm:ss", example = "17:00:00")
    private LocalTime endOfWorkingDay;
}

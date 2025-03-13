package medical_clinics.web.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder

public class NewDaySchedule {

    @NotNull(message = "{not.blank}")
    @Future(message = "{schedule.date}")
    private LocalDate date;

    @NotNull(message = "{not.blank}")
    private LocalTime startTime;

    @NotNull(message = "{not.blank}")
    private LocalTime endTime;

    @NotNull(message = "{not.blank}")
    @Min(value = 15, message = "{schedule.interval}")
    @Max(value = 60, message = "{schedule.interval}")
    private Integer TimeSlotInterval;
}

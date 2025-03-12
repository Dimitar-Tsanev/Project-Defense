package medical_clinics.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import medical_clinics.web.validation.day_of_week.DayOfWeekName;


import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data

public class WorkDayDto {

    @NotNull(message = "{not.blank}")
    @DayOfWeekName(message = "{day.of.week}")
    private String dayName;

    @NotNull(message = "{not.blank}")
    private LocalTime startOfWorkingDay;

    @NotNull(message = "{not.blank}")
    private LocalTime endOfWorkingDay;
}

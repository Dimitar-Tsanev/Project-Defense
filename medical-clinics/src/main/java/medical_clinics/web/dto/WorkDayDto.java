package medical_clinics.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data

public class WorkDayDto {

    String dayName;

    String startOfWorkingDay;

    String endOfWorkingDay;
}

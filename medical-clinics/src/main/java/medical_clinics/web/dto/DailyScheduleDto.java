package medical_clinics.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder

public class DailyScheduleDto {

    private String date;

    private String startTime;

    private String endTime;

    private Integer TimeSlotInterval;
}

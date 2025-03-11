package medical_clinics.schedule.mapper;

import medical_clinics.schedule.models.DailySchedule;
import medical_clinics.web.dto.DailyScheduleDto;

public class DailyScheduleMapper {
    private DailyScheduleMapper () {
    }

    public static DailySchedule mapToDailySchedule ( DailyScheduleDto dailyScheduleDto ) {
        return DailySchedule.builder ( )
                .date ( dailyScheduleDto.getDate ( ) )
                .startTime ( dailyScheduleDto.getStartTime ( ) )
                .endTime ( dailyScheduleDto.getEndTime ( ) )
                .build ( );
    }
}

package medical_clinics.shared.mappers;

import medical_clinics.schedule.models.DailySchedule;
import medical_clinics.web.dto.DailyScheduleDto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DailyScheduleMapper {
    private DailyScheduleMapper () {
    }

    public static DailySchedule mapToDailySchedule ( DailyScheduleDto dailyScheduleDto ) {
        return DailySchedule.builder ( )
                .date (
                        LocalDate.parse (
                                dailyScheduleDto.getDate (),
                                DateTimeFormatter.ofPattern ( "dd MMM yyyy" )
                        )
                )
                .startTime (
                        LocalTime.parse (
                                dailyScheduleDto.getStartTime ( ),
                                DateTimeFormatter.ofPattern ( "HH:mm" )
                        )
                )
                .endTime (
                        LocalTime.parse (
                                dailyScheduleDto.getEndTime ( ),
                                DateTimeFormatter.ofPattern ( "HH:mm" )
                        )
                )
                .build ( );
    }
}

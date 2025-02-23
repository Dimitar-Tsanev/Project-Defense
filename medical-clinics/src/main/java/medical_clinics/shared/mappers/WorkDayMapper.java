package medical_clinics.shared.mappers;

import medical_clinics.clinic.models.DaysOfWeek;
import medical_clinics.clinic.models.WorkDay;
import medical_clinics.web.dto.WorkDayDto;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class WorkDayMapper {
    private WorkDayMapper () {
    }

    public static WorkDayDto mapToDto ( WorkDay workDay ) {
        return WorkDayDto.builder ( )
                .dayName (
                        workDay.getDayOfWeek ( ).name ( ).charAt ( 0 ) +
                                workDay.getDayOfWeek ( ).name ( ).substring ( 1 ).toLowerCase ( )
                )
                .startOfWorkingDay (
                        workDay.getStartOfWorkingDay ( ).format ( DateTimeFormatter.ofPattern ( "HH:mm" ) )
                )
                .endOfWorkingDay (
                        workDay.getEndOfWorkingDay ( ).format ( DateTimeFormatter.ofPattern ( "HH:mm" ) )
                )
                .build ( );

    }

    public static WorkDay mapToModel ( WorkDayDto workDayDto ) {
        return WorkDay.builder ( )
                .dayOfWeek (
                        DaysOfWeek.valueOf ( workDayDto.getDayName ( ).toUpperCase ( ) )
                )
                .startOfWorkingDay (
                        LocalTime.parse ( workDayDto.getStartOfWorkingDay ( ), DateTimeFormatter.ofPattern ( "HH:mm" ) )
                )
                .endOfWorkingDay (
                        LocalTime.parse ( workDayDto.getEndOfWorkingDay (), DateTimeFormatter.ofPattern ( "HH:mm" ) )
                )
                .build ( );
    }
}

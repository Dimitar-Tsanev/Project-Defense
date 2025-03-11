package medical_clinics.clinic.mappers;

import medical_clinics.clinic.models.DaysOfWeek;
import medical_clinics.clinic.models.WorkDay;
import medical_clinics.web.dto.WorkDayDto;

public class WorkDayMapper {
    private WorkDayMapper () {
    }

    public static WorkDayDto mapToDto ( WorkDay workDay ) {
        return WorkDayDto.builder ( )
                .dayName (
                        workDay.getDayOfWeek ( ).name ( ).charAt ( 0 ) +
                                workDay.getDayOfWeek ( ).name ( ).substring ( 1 ).toLowerCase ( )
                )
                .startOfWorkingDay ( workDay.getStartOfWorkingDay ( ) )
                .endOfWorkingDay ( workDay.getEndOfWorkingDay ( ) )
                .build ( );

    }

    public static WorkDay mapToModel ( WorkDayDto workDayDto ) {
        return WorkDay.builder ( )
                .dayOfWeek (
                        DaysOfWeek.valueOf ( workDayDto.getDayName ( ).toUpperCase ( ) )
                )
                .startOfWorkingDay ( workDayDto.getStartOfWorkingDay ( ) )
                .endOfWorkingDay ( workDayDto.getEndOfWorkingDay ( ) )
                .build ( );
    }
}

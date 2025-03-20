package medical_clinics.clinic.mappers;

import medical_clinics.clinic.models.DaysOfWeek;
import medical_clinics.clinic.models.WorkDay;
import medical_clinics.web.dto.WorkDayDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class WorkDayMapperUTests {
    private static final LocalTime START_TIME = LocalTime.of ( 10, 0, 0 );
    private static final LocalTime END_TIME = LocalTime.of ( 17, 0, 0 );

    private static final String DAYS_OF_WEEK = "Monday";
    private static final DaysOfWeek DAYS_OF_WEEK_ENUM = DaysOfWeek.MONDAY;

    @Test
    void when_mapToDto_ExpectWorkdayDto () {
        WorkDay workday = WorkDay.builder ( )
                .dayOfWeek ( DAYS_OF_WEEK_ENUM)
                .startOfWorkingDay ( START_TIME )
                .endOfWorkingDay ( END_TIME )
                .build ( );

        WorkDayDto dto = WorkDayMapper.mapToDto ( workday );

        assertEquals( DAYS_OF_WEEK, dto.getDayName () );
        assertEquals( START_TIME, dto.getStartOfWorkingDay () );
        assertEquals( END_TIME, dto.getEndOfWorkingDay () );
    }

    @Test
    void when_mapToModel_ExpectWorkdayModel () {
        WorkDayDto dto = WorkDayDto.builder()
                .dayName ( DAYS_OF_WEEK )
                .startOfWorkingDay ( START_TIME )
                .endOfWorkingDay ( END_TIME )
                .build();

        WorkDay model = WorkDayMapper.mapToModel ( dto );

        assertEquals ( DAYS_OF_WEEK_ENUM, model.getDayOfWeek () );
        assertEquals ( START_TIME, model.getStartOfWorkingDay () );
        assertEquals ( END_TIME, model.getEndOfWorkingDay () );
    }

    @Test
    void when_mapToDto_with_Empty_Object_ExpectException () {
        WorkDay day = WorkDay.builder().build();

        assertThrows(NullPointerException.class,() ->WorkDayMapper.mapToDto ( day ) );
    }

    @Test
    void when_mapToModel_with_Empty_Object_ExpectException () {
        WorkDayDto dto = WorkDayDto.builder().build();

        assertThrows(NullPointerException.class,() ->WorkDayMapper.mapToModel ( dto) );

    }
}

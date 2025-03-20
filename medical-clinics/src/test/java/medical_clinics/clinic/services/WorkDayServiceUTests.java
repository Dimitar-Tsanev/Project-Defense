package medical_clinics.clinic.services;

import medical_clinics.clinic.models.Clinic;
import medical_clinics.clinic.models.WorkDay;
import medical_clinics.clinic.repositories.WorkDaysRepository;
import medical_clinics.web.dto.WorkDayDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorkDayServiceUTests {
    private static final UUID CLINIC_UUID = UUID.randomUUID ( );

    private static final LocalTime START_TIME = LocalTime.of ( 10, 0, 0 );
    private static final LocalTime END_TIME = LocalTime.of ( 17, 0, 0 );
    @Mock
    private WorkDaysRepository workDaysRepository;

    @InjectMocks
    private WorkDayService workDayService;

    @Test
    void when_addWorkdays_WithEmptyDtoCollection_then_returnEmptyCollectionModels () {
        Collection<WorkDayDto> workDays = new ArrayList<> ( );
        Clinic clinic = new Clinic ( );
        clinic.setId ( CLINIC_UUID );

        Collection<WorkDay> days = workDayService.addWorkdays ( workDays, clinic );

        verify ( workDaysRepository, never ( ) ).save ( any ( ) );
        assertEquals ( 0, days.size ( ) );
    }

    @Test
    void when_addWorkdays_WithValidDtoCollection_then_returnWorkDays () {
        Clinic clinic = new Clinic ( );
        clinic.setId ( CLINIC_UUID );

        Collection<WorkDayDto> workDays = List.of (
                WorkDayDto.builder ( ).dayName ( DayOfWeek.MONDAY.name ( ) ).startOfWorkingDay ( START_TIME )
                        .endOfWorkingDay ( END_TIME ).build ( ),
                WorkDayDto.builder ( ).dayName ( DayOfWeek.THURSDAY.name ( ) ).startOfWorkingDay ( START_TIME )
                        .endOfWorkingDay ( END_TIME ).build ( )
        );

        Collection<WorkDay> days = workDayService.addWorkdays ( workDays, clinic );

        assertEquals ( 2, days.size ( ) );
        verify ( workDaysRepository, times ( 2 ) ).save ( any ( ) );
    }

    @Test
    void when_addWorkdays_WithDuplicateDaysNames_then_OnlyOneObjectIsSaved ( ) {
        Clinic clinic = new Clinic ( );
        clinic.setId ( CLINIC_UUID );

        Collection<WorkDayDto> workDays = List.of (
                WorkDayDto.builder ( ).dayName ( DayOfWeek.MONDAY.name ( ) ).startOfWorkingDay ( START_TIME )
                        .endOfWorkingDay ( END_TIME ).build ( ),
                WorkDayDto.builder ( ).dayName ( DayOfWeek.MONDAY.name ( ) ).startOfWorkingDay ( START_TIME )
                        .endOfWorkingDay ( END_TIME ).build ( )
        );

        Collection<WorkDay> days = workDayService.addWorkdays ( workDays, clinic );

        assertEquals ( 1, days.size ( ) );
        verify ( workDaysRepository, times ( 1 ) ).save ( any ( ) );
    }
}

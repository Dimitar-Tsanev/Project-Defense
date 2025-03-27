package medical_clinics.clinic.services;

import medical_clinics.clinic.models.Clinic;
import medical_clinics.clinic.models.DaysOfWeek;
import medical_clinics.clinic.models.WorkDay;
import medical_clinics.clinic.repositories.ClinicRepository;
import medical_clinics.clinic.repositories.WorkDaysRepository;
import medical_clinics.web.dto.WorkDayDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class WorkdayServiceITests {
    private static final String STRING = "test";
    private static final String PICTURE_URL = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/bd/Test.svg/1200px-Test.svg.png";

    @Autowired
    WorkDayService workDayService;

    @Autowired
    WorkDaysRepository workDaysRepository;

    @Autowired
    ClinicRepository clinicRepository;

    private Clinic clinic;
    private List<WorkDay> initialWorkDays;

    @BeforeEach
    void setUp () {
        Clinic clinic = Clinic.builder ( )
                .city ( STRING )
                .address ( STRING )
                .workingDays ( new ArrayList<> ( ) )
                .specialties ( new ArrayList<> ( ) )
                .pictureUrl ( PICTURE_URL )
                .description ( STRING )
                .phoneNumber ( "+1234567890" )
                .identificationNumber ( "AA123456789" )
                .build ( );

        this.clinic = clinicRepository.save ( clinic );

        List<WorkDay> workDays = List.of (
                WorkDay.builder ( )
                        .dayOfWeek ( DaysOfWeek.MONDAY )
                        .startOfWorkingDay ( LocalTime.of ( 10, 0 ) )
                        .endOfWorkingDay ( LocalTime.of ( 17, 0 ) )
                        .clinic ( this.clinic )
                        .build ( ),
                WorkDay.builder ( )
                        .dayOfWeek ( DaysOfWeek.TUESDAY )
                        .startOfWorkingDay ( LocalTime.of ( 10, 0 ) )
                        .endOfWorkingDay ( LocalTime.of ( 17, 0 ) )
                        .clinic ( this.clinic )
                        .build ( ),
                WorkDay.builder ( )
                        .dayOfWeek ( DaysOfWeek.WEDNESDAY )
                        .startOfWorkingDay ( LocalTime.of ( 10, 0 ) )
                        .endOfWorkingDay ( LocalTime.of ( 17, 0 ) )
                        .clinic ( this.clinic )
                        .build ( )
        );

        this.initialWorkDays = workDaysRepository.saveAll ( workDays );

    }

    @Test
    void when_updateWorkdays_withDaysAlreadyExists_thenWorkdaysAreUpdated () {
        Collection<WorkDayDto> workDaysUpdate = List.of (
                WorkDayDto.builder ( )
                        .dayName ( DaysOfWeek.MONDAY.name ( ) )
                        .startOfWorkingDay ( LocalTime.of ( 8, 0 ) )
                        .endOfWorkingDay ( LocalTime.of ( 18, 0 ) )
                        .build ( ),
                WorkDayDto.builder ( )
                        .dayName ( DaysOfWeek.TUESDAY.name ( ) )
                        .startOfWorkingDay ( LocalTime.of ( 8, 0 ) )
                        .endOfWorkingDay ( LocalTime.of ( 18, 0 ) )
                        .build ( ),
                WorkDayDto.builder ( )
                        .dayName ( DaysOfWeek.WEDNESDAY.name ( ) )
                        .startOfWorkingDay ( LocalTime.of ( 8, 0 ) )
                        .endOfWorkingDay ( LocalTime.of ( 18, 0 ) )
                        .build ( )
        );

        List<WorkDay> updatedWorkdays = (List<WorkDay>) workDayService.updateWorkDays ( clinic, workDaysUpdate );

        List<UUID> initialWorkdaysIds = initialWorkDays.stream ( ).map ( WorkDay::getId ).toList ( );
        List<DaysOfWeek> initialWorkdaysNames = initialWorkDays.stream ( ).map ( WorkDay::getDayOfWeek ).toList ( );

        assertEquals ( initialWorkDays.size ( ), updatedWorkdays.size ( ) );

        for ( WorkDay workDay : updatedWorkdays ) {
            assertEquals ( workDay.getClinic ( ).getId ( ), clinic.getId ( ) );

            assertTrue ( initialWorkdaysIds.contains ( workDay.getId ( ) ) );
            assertTrue ( initialWorkdaysNames.contains ( workDay.getDayOfWeek ( ) ) );

            assertEquals ( LocalTime.of ( 8, 0 ), workDay.getStartOfWorkingDay ( ) );
            assertEquals ( LocalTime.of ( 18, 0 ), workDay.getEndOfWorkingDay ( ) );
        }
    }

    @Test
    void when_updateWorkdays_withRemovedDayAndAlreadyExistingDays_thenWorkdaysAreUpdated () {
        Collection<WorkDayDto> workDaysUpdate = List.of (
                WorkDayDto.builder ( )
                        .dayName ( DaysOfWeek.MONDAY.name ( ) )
                        .startOfWorkingDay ( LocalTime.of ( 8, 0 ) )
                        .endOfWorkingDay ( LocalTime.of ( 18, 0 ) )
                        .build ( ),
                WorkDayDto.builder ( )
                        .dayName ( DaysOfWeek.TUESDAY.name ( ) )
                        .startOfWorkingDay ( LocalTime.of ( 8, 0 ) )
                        .endOfWorkingDay ( LocalTime.of ( 18, 0 ) )
                        .build ( )
        );

        List<WorkDay> updatedWorkdays = (List<WorkDay>) workDayService.updateWorkDays ( clinic, workDaysUpdate );

        List<UUID> initialWorkdaysIds = initialWorkDays.stream ( ).map ( WorkDay::getId ).toList ( );
        List<DaysOfWeek> initialWorkdaysNames = initialWorkDays.stream ( ).map ( WorkDay::getDayOfWeek ).toList ( );

        assertTrue ( initialWorkDays.size ( ) > updatedWorkdays.size ( ) );

        assertFalse (
                updatedWorkdays.stream ( ).map ( WorkDay::getDayOfWeek ).toList ( ).contains ( DaysOfWeek.WEDNESDAY )
        );

        for ( WorkDay workDay : updatedWorkdays ) {
            assertEquals ( workDay.getClinic ( ).getId ( ), clinic.getId ( ) );

            assertTrue ( initialWorkdaysIds.contains ( workDay.getId ( ) ) );
            assertTrue ( initialWorkdaysNames.contains ( workDay.getDayOfWeek ( ) ) );

            assertEquals ( LocalTime.of ( 8, 0 ), workDay.getStartOfWorkingDay ( ) );
            assertEquals ( LocalTime.of ( 18, 0 ), workDay.getEndOfWorkingDay ( ) );
        }
    }

    @Test
    void when_updateWorkdays_withNewDayAndAlreadyExistingDays_thenWorkdaysAreUpdated () {
        Collection<WorkDayDto> workDaysUpdate = List.of (
                WorkDayDto.builder ( )
                        .dayName ( DaysOfWeek.MONDAY.name ( ) )
                        .startOfWorkingDay ( LocalTime.of ( 8, 0 ) )
                        .endOfWorkingDay ( LocalTime.of ( 18, 0 ) )
                        .build ( ),
                WorkDayDto.builder ( )
                        .dayName ( DaysOfWeek.TUESDAY.name ( ) )
                        .startOfWorkingDay ( LocalTime.of ( 8, 0 ) )
                        .endOfWorkingDay ( LocalTime.of ( 18, 0 ) )
                        .build ( ),
                WorkDayDto.builder ( )
                        .dayName ( DaysOfWeek.WEDNESDAY.name ( ) )
                        .startOfWorkingDay ( LocalTime.of ( 8, 0 ) )
                        .endOfWorkingDay ( LocalTime.of ( 18, 0 ) )
                        .build ( ),
                WorkDayDto.builder ( )
                        .dayName ( DaysOfWeek.THURSDAY.name ( ) )
                        .startOfWorkingDay ( LocalTime.of ( 8, 0 ) )
                        .endOfWorkingDay ( LocalTime.of ( 18, 0 ) )
                        .build ( )
        );

        List<WorkDay> updatedWorkdays = (List<WorkDay>) workDayService.updateWorkDays ( clinic, workDaysUpdate );

        List<UUID> initialWorkdaysIds = initialWorkDays.stream ( ).map ( WorkDay::getId ).toList ( );
        List<DaysOfWeek> initialWorkdaysNames = initialWorkDays.stream ( ).map ( WorkDay::getDayOfWeek ).toList ( );

        assertEquals ( initialWorkDays.size ( ) + 1, updatedWorkdays.size ( ) );

        assertTrue (
                updatedWorkdays.stream ( ).map ( WorkDay::getDayOfWeek ).toList ( ).contains ( DaysOfWeek.THURSDAY )
        );

        for ( WorkDay workDay : updatedWorkdays ) {
            if (workDay.getDayOfWeek ().equals ( DaysOfWeek.THURSDAY )) {
                assertFalse ( initialWorkdaysIds.contains ( workDay.getId ( ) ) );
                assertFalse ( initialWorkdaysNames.contains ( workDay.getDayOfWeek ( ) ) );

            }else {
                assertTrue ( initialWorkdaysIds.contains ( workDay.getId ( ) ) );
                assertTrue ( initialWorkdaysNames.contains ( workDay.getDayOfWeek ( ) ) );
            }

            assertEquals ( workDay.getClinic ( ).getId ( ), clinic.getId ( ) );

            assertEquals ( LocalTime.of ( 8, 0 ), workDay.getStartOfWorkingDay ( ) );
            assertEquals ( LocalTime.of ( 18, 0 ), workDay.getEndOfWorkingDay ( ) );
        }
    }
}

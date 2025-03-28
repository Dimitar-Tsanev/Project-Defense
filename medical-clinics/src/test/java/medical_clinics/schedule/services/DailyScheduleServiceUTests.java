package medical_clinics.schedule.services;

import medical_clinics.clinic.models.Clinic;
import medical_clinics.clinic.models.DaysOfWeek;
import medical_clinics.clinic.models.WorkDay;
import medical_clinics.patient.model.Patient;
import medical_clinics.physician.model.Physician;
import medical_clinics.schedule.exceptions.ScheduleConflictException;
import medical_clinics.schedule.exceptions.ScheduleNotFoundException;
import medical_clinics.schedule.models.DailySchedule;
import medical_clinics.schedule.models.Status;
import medical_clinics.schedule.models.TimeSlot;
import medical_clinics.schedule.repositories.DailyScheduleRepository;
import medical_clinics.web.dto.NewDaySchedule;
import medical_clinics.web.dto.response.schedule_private.PhysicianDaySchedulePrivate;
import medical_clinics.web.dto.response.schedule_public.PhysicianDaySchedulePublic;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DailyScheduleServiceUTests {

    @Mock
    DailyScheduleRepository dailyScheduleRepository;

    @Mock
    TimeSlotService timeSlotService;

    @InjectMocks
    DailyScheduleService dailyScheduleService;

    @Test
    void when_getPrivatePhysicianSchedules_withSchedules_ReturnPrivateSchedules () {
        UUID physicianId = UUID.randomUUID ( );

        List<DailySchedule> dailySchedules = List.of (
                DailySchedule.builder ( )
                        .date ( LocalDate.of ( 2025, 5, 5 ) )
                        .timeSlots ( List.of (
                                TimeSlot.builder ( )
                                        .startTime ( LocalTime.of ( 10, 0 ) )
                                        .status ( Status.FREE )
                                        .durationInMinutes ( 15 )
                                        .patient ( new Patient ( ) )
                                        .build ( ),
                                TimeSlot.builder ( )
                                        .startTime ( LocalTime.of ( 10, 15 ) )
                                        .status ( Status.FREE )
                                        .durationInMinutes ( 15 )
                                        .patient ( new Patient ( ) )
                                        .build ( )
                        ) )
                        .build ( )
        );

        when ( dailyScheduleRepository.findAllByPhysician_UserAccount_IdOrderByDateAsc ( physicianId ) )
                .thenReturn ( dailySchedules );

        List<PhysicianDaySchedulePrivate> schedules = dailyScheduleService.getPrivatePhysicianSchedules ( physicianId );

        assertEquals ( dailySchedules.size ( ), schedules.size ( ) );
        assertEquals ( dailySchedules.getFirst ( ).getDate ( ), schedules.getFirst ( ).getDate ( ) );
        assertEquals ( dailySchedules.getFirst ( ).getTimeSlots ( ).size ( ), schedules.getFirst ( ).getSchedule ( ).size ( ) );
        assertNotNull ( schedules.getFirst ( ).getSchedule ( ).getFirst ( ).getPatientInfo ( ) );

        verify ( dailyScheduleRepository, times ( 1 ) )
                .findAllByPhysician_UserAccount_IdOrderByDateAsc ( physicianId );
    }

    @Test
    void when_getPrivatePhysicianSchedules_withNoSchedules_ReturnPrivateSchedulesEmpty () {
        UUID physicianId = UUID.randomUUID ( );

        when ( dailyScheduleRepository.findAllByPhysician_UserAccount_IdOrderByDateAsc ( physicianId ) )
                .thenReturn ( new ArrayList<> ( ) );

        List<PhysicianDaySchedulePrivate> schedules = dailyScheduleService.getPrivatePhysicianSchedules ( physicianId );

        assertTrue ( schedules.isEmpty ( ) );
        verify ( dailyScheduleRepository, times ( 1 ) )
                .findAllByPhysician_UserAccount_IdOrderByDateAsc ( physicianId );
    }

    @Test
    void when_getPublicPhysicianSchedules_withSchedules_ReturnPrivateSchedules () {
        UUID physicianId = UUID.randomUUID ( );

        List<DailySchedule> dailySchedules = List.of (
                DailySchedule.builder ( )
                        .date ( LocalDate.of ( 2025, 5, 5 ) )
                        .timeSlots ( List.of (
                                TimeSlot.builder ( )
                                        .startTime ( LocalTime.of ( 10, 0 ) )
                                        .status ( Status.FREE )
                                        .durationInMinutes ( 15 )
                                        .patient ( new Patient ( ) )
                                        .build ( ),
                                TimeSlot.builder ( )
                                        .startTime ( LocalTime.of ( 10, 15 ) )
                                        .status ( Status.FREE )
                                        .durationInMinutes ( 15 )
                                        .patient ( new Patient ( ) )
                                        .build ( )
                        ) )
                        .build ( )
        );

        when ( dailyScheduleRepository.findAllByPhysicianIdOrderByDateAsc ( physicianId ) )
                .thenReturn ( dailySchedules );

        List<PhysicianDaySchedulePublic> schedules = dailyScheduleService.getPublicPhysicianSchedules ( physicianId );

        assertEquals ( dailySchedules.size ( ), schedules.size ( ) );
        assertEquals ( dailySchedules.getFirst ( ).getDate ( ), schedules.getFirst ( ).getDate ( ) );
        assertEquals ( dailySchedules.getFirst ( ).getTimeSlots ( ).size ( ), schedules.getFirst ( ).getSchedule ( ).size ( ) );
        verify ( dailyScheduleRepository, times ( 1 ) )
                .findAllByPhysicianIdOrderByDateAsc ( physicianId );
    }

    @Test
    void when_getPublicPhysicianSchedules_withNoSchedules_ReturnPrivateSchedulesEmpty () {
        UUID physicianId = UUID.randomUUID ( );

        when ( dailyScheduleRepository.findAllByPhysicianIdOrderByDateAsc ( physicianId ) )
                .thenReturn ( new ArrayList<> ( ) );

        List<PhysicianDaySchedulePublic> schedules = dailyScheduleService.getPublicPhysicianSchedules ( physicianId );

        assertTrue ( schedules.isEmpty ( ) );
        verify ( dailyScheduleRepository, times ( 1 ) )
                .findAllByPhysicianIdOrderByDateAsc ( physicianId );
    }

    @Test
    void when_generateDaySchedule_withEndTimeBeforeStatTime_ShouldThrowException () {
        NewDaySchedule newDaySchedule = NewDaySchedule.builder ( )
                .startTime ( LocalTime.now ( ).plusMinutes ( 30 ) )
                .endTime ( LocalTime.now ( ) )
                .build ( );

        assertThrows ( ScheduleConflictException.class,
                () -> dailyScheduleService.generateDaySchedule ( new Physician ( ), newDaySchedule ) );
    }

    @Test
    void when_generateDaySchedule_withStartTimeBeforeWorkDayOfTheClinic_ShouldThrowException () {
        NewDaySchedule newDaySchedule = NewDaySchedule.builder ( )
                .date ( LocalDate.now ( ) )
                .startTime ( LocalTime.now ( ) )
                .endTime ( LocalTime.now ( ).plusMinutes ( 30 ) )
                .build ( );

        WorkDay workDay = WorkDay.builder ( )
                .dayOfWeek ( DaysOfWeek.valueOf ( LocalDate.now ( ).getDayOfWeek ( ).toString ( ) ) )
                .startOfWorkingDay ( LocalTime.now ( ).plusMinutes ( 30 ) )
                .endOfWorkingDay ( LocalTime.now ( ).plusMinutes ( 60 ) )
                .build ( );

        assertThrows ( ScheduleConflictException.class,
                () -> dailyScheduleService.generateDaySchedule ( buildPhysician ( workDay ), newDaySchedule ) );
    }

    @Test
    void when_generateDaySchedule_withEndTimeIsAfterEndOfWorkDayOfTheClinic_ShouldThrowException () {
        NewDaySchedule newDaySchedule = NewDaySchedule.builder ( )
                .date ( LocalDate.now ( ) )
                .startTime ( LocalTime.now ( ).minusMinutes ( 30 ) )
                .endTime ( LocalTime.now ( ).plusMinutes ( 30 ) )
                .build ( );

        WorkDay workDay = WorkDay.builder ( )
                .dayOfWeek ( DaysOfWeek.valueOf ( LocalDate.now ( ).getDayOfWeek ( ).toString ( ) ) )
                .startOfWorkingDay ( LocalTime.now ( ).minusHours ( 1 ) )
                .endOfWorkingDay ( LocalTime.now ( ) )
                .build ( );

        assertThrows ( ScheduleConflictException.class,
                () -> dailyScheduleService.generateDaySchedule ( buildPhysician ( workDay ), newDaySchedule ) );
    }

    @Test
    void when_generateDaySchedule_withDayOfScheduleWhenClinicDontWork_ShouldThrowException () {
        NewDaySchedule newDaySchedule = NewDaySchedule.builder ( )
                .date ( LocalDate.now ( ) )
                .startTime ( LocalTime.now ( ).minusMinutes ( 30 ) )
                .endTime ( LocalTime.now ( ).plusMinutes ( 30 ) )
                .build ( );

        WorkDay workDay = WorkDay.builder ( )
                .dayOfWeek ( DaysOfWeek.valueOf ( LocalDate.now ( ).plusDays ( 1 ).getDayOfWeek ( ).toString ( ) ) )
                .startOfWorkingDay ( LocalTime.now ( ).minusHours ( 1 ) )
                .endOfWorkingDay ( LocalTime.now ( ).plusHours ( 1 ) )
                .build ( );

        assertThrows ( ScheduleConflictException.class,
                () -> dailyScheduleService.generateDaySchedule ( buildPhysician ( workDay ), newDaySchedule ) );
    }

    @Test
    void when_inactivateDaySchedule_WithNotFoundPhysicianSchedule_ShouldThrowException () {
        when ( dailyScheduleRepository.findAllByPhysician_UserAccount_IdAndDate ( any ( ), any ( ) ) )
                .thenReturn ( Optional.empty ( ) );

        assertThrows ( ScheduleNotFoundException.class,
                () -> dailyScheduleService.inactivateDaySchedule ( UUID.randomUUID ( ), LocalDate.now ( ) ) );

        verify ( dailyScheduleRepository, times ( 1 ) )
                .findAllByPhysician_UserAccount_IdAndDate ( any ( ), any ( ) );
    }

    @Test
    void when_inactivateDaySchedule_WithFoundPhysicianSchedule_ShouldCallTimeslotInactive () {
        DailySchedule schedule = DailySchedule.builder ( )
                .date ( LocalDate.now ( ) )
                .timeSlots ( List.of (
                        TimeSlot.builder ( )
                                .startTime ( LocalTime.of ( 10, 0 ) )
                                .status ( Status.FREE )
                                .durationInMinutes ( 15 )
                                .build ( ),
                        TimeSlot.builder ( )
                                .startTime ( LocalTime.of ( 10, 15 ) )
                                .status ( Status.FREE )
                                .durationInMinutes ( 15 )
                                .build ( )
                ) )
                .build ( );

        when ( dailyScheduleRepository.findAllByPhysician_UserAccount_IdAndDate ( any ( ), any ( ) ) )
                .thenReturn ( Optional.of ( schedule ) );

        dailyScheduleService.inactivateDaySchedule ( UUID.randomUUID ( ), LocalDate.now ( ) );

        verify ( dailyScheduleRepository, times ( 1 ) )
                .findAllByPhysician_UserAccount_IdAndDate ( any ( ), any ( ) );

        verify ( timeSlotService,times ( schedule.getTimeSlots ().size () ) ).inactivate ( any ( ) );
    }

    private Physician buildPhysician ( WorkDay workDay ) {
        return Physician.builder ( ).workplace ( buildClinic ( workDay ) ).build ( );
    }

    private Clinic buildClinic ( WorkDay workDay ) {
        return Clinic.builder ( ).workingDays ( List.of ( workDay ) ).build ( );
    }
}

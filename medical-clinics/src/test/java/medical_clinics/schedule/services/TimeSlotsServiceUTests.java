package medical_clinics.schedule.services;

import medical_clinics.clinic.models.Clinic;
import medical_clinics.patient.model.Patient;
import medical_clinics.physician.model.Physician;
import medical_clinics.schedule.exceptions.ScheduleConflictException;
import medical_clinics.schedule.exceptions.ScheduleNotFoundException;
import medical_clinics.schedule.models.DailySchedule;
import medical_clinics.schedule.models.Status;
import medical_clinics.schedule.models.TimeSlot;
import medical_clinics.schedule.repositories.TimeSlotRepository;
import medical_clinics.specialty.model.Specialty;
import medical_clinics.specialty.model.SpecialtyName;
import medical_clinics.user_account.model.UserAccount;
import medical_clinics.web.dto.response.PatientAppointment;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TimeSlotsServiceUTests {

    @Mock
    TimeSlotRepository timeSlotRepository;

    @InjectMocks
    TimeSlotService timeSlotService;

    @Test
    void when_generateTimeSlots_ShouldReturnCollectionOfGeneratedTimeslots () {
        UUID scheduleId = UUID.randomUUID ( );
        DailySchedule schedule = DailySchedule.builder ( ).id ( scheduleId ).build ( );

        when ( timeSlotRepository.save ( any ( ) ) ).thenReturn (
                TimeSlot.builder ( ).id ( UUID.randomUUID ( ) ).dailySchedule ( schedule ).build ( )
        );

        List<TimeSlot> timeSlots = (List<TimeSlot>) timeSlotService.generateTimeSlots (
                LocalTime.of ( 10, 0 ), LocalTime.of ( 11, 0 ), 30, schedule
        );

        assertEquals ( 2, timeSlots.size ( ) );
        assertEquals ( timeSlots.getFirst ( ).getDailySchedule ( ).getId ( ), scheduleId );

        verify ( timeSlotRepository, times ( 2 ) ).save ( any ( ) );
    }

    @Test
    void when_inactivate_withIdNotFound_shouldThrowException () {
        UUID id = UUID.randomUUID ( );
        when ( timeSlotRepository.findById ( id ) ).thenReturn ( Optional.empty ( ) );

        assertThrows ( ScheduleNotFoundException.class,
                () -> timeSlotService.inactivate ( id ),
                "TimeSlot with id " + id + " not found"
        );
        verify ( timeSlotRepository, never ( ) ).save ( any ( ) );
        verify ( timeSlotRepository, times ( 1 ) ).findById ( id );
    }

    @Test
    void when_inactivate_withTimeSlotAppointed_shouldThrowException () {
        UUID id = UUID.randomUUID ( );

        TimeSlot timeSlot = TimeSlot.builder ( )
                .id ( id )
                .startTime ( LocalTime.of ( 16, 0 ) )
                .dailySchedule (
                        DailySchedule.builder ( )
                                .id ( UUID.randomUUID ( ) )
                                .date ( LocalDate.now ( ) )
                                .build ( )
                )
                .patient ( Patient.builder ( )
                        .phone ( "123456789" )
                        .email ( "test@email.com" )
                        .firstName ( "Test" )
                        .lastName ( "Test" )
                        .build ( )
                )
                .build ( );

        when ( timeSlotRepository.findById ( id ) ).thenReturn ( Optional.of ( timeSlot ) );

        assertThrows ( ScheduleConflictException.class, () -> timeSlotService.inactivate ( id ) );
        verify ( timeSlotRepository, never ( ) ).save ( any ( ) );
        verify ( timeSlotRepository, times ( 1 ) ).findById ( id );
    }

    @Test
    void when_inactivate_withTimeSlotFree_shouldInactivate () {
        UUID id = UUID.randomUUID ( );

        TimeSlot timeSlot = TimeSlot.builder ( )
                .id ( id )
                .startTime ( LocalTime.of ( 16, 0 ) )
                .dailySchedule (
                        new DailySchedule ( )
                )
                .build ( );

        when ( timeSlotRepository.findById ( id ) ).thenReturn ( Optional.of ( timeSlot ) );

        timeSlotService.inactivate ( id );

        verify ( timeSlotRepository, times ( 1 ) ).save ( any ( ) );
        verify ( timeSlotRepository, times ( 1 ) ).findById ( id );
    }

    @Test
    void when_delete_ShouldDeleteTimeSlot () {
        timeSlotService.delete ( new TimeSlot ( ) );
        verify ( timeSlotRepository, times ( 1 ) ).delete ( any ( ) );
    }

    @Test
    void when_getPatientAppointments_withNoAppointments_shouldReturnEmptyList () {
        when ( timeSlotRepository.findAllByPatient_Id ( any ( ) ) ).thenReturn ( new ArrayList<> ( ) );

        timeSlotService.getPatientAppointments ( UUID.randomUUID ( ) );

        verify ( timeSlotRepository, times ( 1 ) ).findAllByPatient_Id ( any ( ) );
    }

    @Test
    void when_getPatientAppointments_withAppointments_shouldReturnAppointments () {
        UUID id = UUID.randomUUID ( );
        UUID patientId = UUID.randomUUID ( );

        TimeSlot timeSlot = TimeSlot.builder ( )
                .id ( id )
                .startTime ( LocalTime.of ( 16, 0 ) )
                .dailySchedule ( buildDailySchedule ( ) )
                .patient ( Patient.builder ( ).id ( patientId ).build ( ) )
                .build ( );

        when ( timeSlotRepository.findAllByPatient_Id ( patientId ) ).thenReturn ( List.of ( timeSlot ) );

        List<PatientAppointment> appointments = timeSlotService.getPatientAppointments ( patientId );

        assertEquals ( 1, appointments.size ( ) );
        assertEquals ( id, appointments.getFirst ( ).getTimeslotId ( ) );
        assertEquals ( LocalTime.of ( 16, 0 ), appointments.getFirst ( ).getStartTime ( ) );

        verify ( timeSlotRepository, times ( 1 ) ).findAllByPatient_Id ( patientId );
    }

    @Test
    void when_makeAppointment_WithTimeSlotNotFound_shouldThrowException () {
        UUID id = UUID.randomUUID ( );

        when ( timeSlotRepository.findById ( id ) ).thenReturn ( Optional.empty ( ) );

        assertThrows ( ScheduleNotFoundException.class,
                () -> timeSlotService.makeAppointment ( UUID.randomUUID ( ), id ) );
    }

    @Test
    void when_makeAppointment_WithTimeSlotPassedTime_shouldThrowException () {
        UUID id = UUID.randomUUID ( );

        TimeSlot timeSlot = TimeSlot.builder ( )
                .id ( id )
                .startTime ( LocalTime.now ( ).minusMinutes ( 30 ) )
                .dailySchedule ( DailySchedule.builder ( ).date ( LocalDate.now ( ) ).build ( ) )
                .status ( Status.FREE )
                .build ( );

        when ( timeSlotRepository.findById ( id ) ).thenReturn ( Optional.of ( timeSlot ) );

        assertThrows ( ScheduleConflictException.class,
                () -> timeSlotService.makeAppointment ( UUID.randomUUID ( ), id )
        );

        verify ( timeSlotRepository, times ( 1 ) ).findById ( id );
        verify ( timeSlotRepository, times ( 1 ) ).save ( timeSlot );
    }

    @Test
    void when_makeAppointment_WithTimeSlotPassedDate_shouldThrowException () {
        UUID id = UUID.randomUUID ( );

        TimeSlot timeSlot = TimeSlot.builder ( )
                .id ( id )
                .startTime ( LocalTime.now ( ).plusMinutes ( 30 ) )
                .dailySchedule ( DailySchedule.builder ( ).date ( LocalDate.now ( ).minusDays ( 1 ) ).build ( ) )
                .status ( Status.FREE )
                .build ( );

        when ( timeSlotRepository.findById ( id ) ).thenReturn ( Optional.of ( timeSlot ) );

        assertThrows ( ScheduleConflictException.class,
                () -> timeSlotService.makeAppointment ( UUID.randomUUID ( ), id )
        );

        verify ( timeSlotRepository, times ( 1 ) ).findById ( id );
        verify ( timeSlotRepository, times ( 1 ) ).save ( timeSlot );
    }

    @Test
    void when_makeAppointment_WithTimeSlotReserved_shouldThrowException () {
        UUID id = UUID.randomUUID ( );

        TimeSlot timeSlot = TimeSlot.builder ( )
                .id ( id )
                .startTime ( LocalTime.now ( ).plusMinutes ( 30 ) )
                .dailySchedule ( DailySchedule.builder ( ).date ( LocalDate.now ( ) ).build ( ) )
                .status ( Status.RESERVED )
                .build ( );

        when ( timeSlotRepository.findById ( id ) ).thenReturn ( Optional.of ( timeSlot ) );

        assertThrows ( ScheduleConflictException.class,
                () -> timeSlotService.makeAppointment ( UUID.randomUUID ( ), id )
        );

        verify ( timeSlotRepository, times ( 1 ) ).findById ( id );
        verify ( timeSlotRepository, times ( 1 ) ).save ( timeSlot );
    }

    @Test
    void when_makeAppointment_WithTimeSlotInactive_shouldThrowException () {
        UUID id = UUID.randomUUID ( );

        TimeSlot timeSlot = TimeSlot.builder ( )
                .id ( id )
                .startTime ( LocalTime.now ( ).plusMinutes ( 30 ) )
                .dailySchedule ( DailySchedule.builder ( ).date ( LocalDate.now ( ) ).build ( ) )
                .status ( Status.INACTIVE )
                .build ( );

        when ( timeSlotRepository.findById ( id ) ).thenReturn ( Optional.of ( timeSlot ) );

        assertThrows ( ScheduleConflictException.class,
                () -> timeSlotService.makeAppointment ( UUID.randomUUID ( ), id )
        );

        verify ( timeSlotRepository, times ( 1 ) ).findById ( id );
        verify ( timeSlotRepository, times ( 1 ) ).save ( timeSlot );
    }

    @Test
    void when_releaseAppointment_WithTimeSlotNotFound_shouldThrowException () {
        UUID id = UUID.randomUUID ( );

        when ( timeSlotRepository.findById ( id ) ).thenReturn ( Optional.empty ( ) );

        assertThrows ( ScheduleNotFoundException.class,
                () -> timeSlotService.releaseAppointment ( UUID.randomUUID ( ), id ) );

        verify ( timeSlotRepository, times ( 1 ) ).findById ( id );
        verify ( timeSlotRepository, never ( ) ).save ( any ( ) );
    }

    @Test
    void when_releaseAppointment_WithConflictPatientId_shouldThrowException () {
        UUID id = UUID.randomUUID ( );

        TimeSlot timeSlot = TimeSlot.builder ( )
                .id ( id )
                .startTime ( LocalTime.now ( ).plusMinutes ( 30 ) )
                .dailySchedule ( DailySchedule.builder ( ).date ( LocalDate.now ( ) ).build ( ) )
                .status ( Status.RESERVED )
                .patient ( Patient.builder ( )
                        .userAccount ( UserAccount.builder ( ).id ( UUID.randomUUID ( ) ).build ( ) ).build ( )
                )
                .build ( );

        when ( timeSlotRepository.findById ( id ) ).thenReturn ( Optional.of ( timeSlot ) );

        assertThrows ( ScheduleConflictException.class,
                () -> timeSlotService.releaseAppointment ( UUID.randomUUID ( ), id ) );

        verify ( timeSlotRepository, times ( 1 ) ).findById ( id );
        verify ( timeSlotRepository, never ( ) ).save ( timeSlot );
    }

    @Test
    void when_releaseAppointment_WithPassedTime_shouldHappenedNothing () {
        UUID id = UUID.randomUUID ( );
        UUID accountId = UUID.randomUUID ( );

        TimeSlot timeSlot = TimeSlot.builder ( )
                .id ( id )
                .startTime ( LocalTime.now ( ).minusMinutes ( 30 ) )
                .dailySchedule ( DailySchedule.builder ( ).date ( LocalDate.now ( ) ).build ( ) )
                .status ( Status.RESERVED )
                .patient ( Patient.builder ( )
                        .userAccount ( UserAccount.builder ( ).id ( accountId ).build ( ) ).build ( )
                )
                .build ( );

        when ( timeSlotRepository.findById ( id ) ).thenReturn ( Optional.of ( timeSlot ) );

        timeSlotService.releaseAppointment ( accountId, id );

        verify ( timeSlotRepository, times ( 1 ) ).findById ( id );
        verify ( timeSlotRepository, never ( ) ).save ( timeSlot );
    }

    private DailySchedule buildDailySchedule () {
        return DailySchedule.builder ( )
                .id ( UUID.randomUUID ( ) )
                .physician (
                        Physician.builder ( )
                                .firstName ( "Test" )
                                .lastName ( "Test" )
                                .specialty ( new Specialty ( SpecialtyName.ALLERGIST ) )
                                .workplace ( Clinic.builder ( )
                                        .city ( "Test" )
                                        .address ( "Test" )
                                        .build ( ) )
                                .build ( )
                )
                .build ( );
    }
}

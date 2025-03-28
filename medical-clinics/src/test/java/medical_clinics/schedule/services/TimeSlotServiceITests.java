package medical_clinics.schedule.services;

import jakarta.transaction.Transactional;
import medical_clinics.patient.model.Patient;
import medical_clinics.patient.repository.PatientRepository;
import medical_clinics.physician.model.Physician;
import medical_clinics.physician.repository.PhysicianRepository;
import medical_clinics.schedule.exceptions.ScheduleConflictException;
import medical_clinics.schedule.models.DailySchedule;
import medical_clinics.schedule.models.Status;
import medical_clinics.schedule.models.TimeSlot;
import medical_clinics.schedule.repositories.DailyScheduleRepository;
import medical_clinics.schedule.repositories.TimeSlotRepository;
import medical_clinics.specialty.model.Specialty;
import medical_clinics.specialty.model.SpecialtyName;
import medical_clinics.specialty.repository.SpecialtyRepository;
import medical_clinics.user_account.model.Role;
import medical_clinics.user_account.model.UserAccount;
import medical_clinics.user_account.model.UserStatus;
import medical_clinics.user_account.repository.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class TimeSlotServiceITests {

    @Autowired
    TimeSlotService timeSlotService;

    @Autowired
    TimeSlotRepository timeSlotRepository;

    @Autowired
    DailyScheduleRepository dailyScheduleRepository;

    @Autowired
    SpecialtyRepository specialtyRepository;

    @Autowired
    PhysicianRepository physicianRepository;

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    UserAccountRepository userAccountRepository;

    @Test
    void when_generateTimeSlots_timeslots_shouldReturnCollectionOfGeneratedTimeslots () {
        int interval = 30;

        DailySchedule dailySchedule = dailyScheduleRepository.save (
                DailySchedule.builder ( )
                        .startTime ( LocalTime.of ( 10, 0 ) )
                        .endTime ( LocalTime.of ( 11, 0 ) )
                        .date ( LocalDate.of ( 2025, 5, 1 ) )
                        .physician ( buildPhysician ( ) )
                        .build ( )
        );

        List<TimeSlot> timeSlots = (List<TimeSlot>) timeSlotService.generateTimeSlots (
                dailySchedule.getStartTime ( ), dailySchedule.getEndTime ( ), interval, dailySchedule
        );

        assertEquals ( 2, timeSlots.size ( ) );
        assertEquals ( timeSlots.getFirst ( ).getDailySchedule ( ).getId ( ), dailySchedule.getId ( ) );
        assertEquals ( timeSlots.getFirst ( ).getStartTime ( ), dailySchedule.getStartTime ( ) );
        assertFalse (
                timeSlots.getLast ( ).getStartTime ( ).isAfter ( dailySchedule.getEndTime ( ).minusMinutes ( interval ) )
        );
        assertEquals ( interval, timeSlots.getFirst ( ).getDurationInMinutes ( ) );
        assertEquals ( Status.FREE, timeSlots.getFirst ( ).getStatus ( ) );
    }

    @Test
    void when_checkForPassedTimeSlots_andHavePassedTimeslots_shouldReplaceStatusToPassed () {
        DailySchedule dailySchedule = dailyScheduleRepository.save (
                DailySchedule.builder ( )
                        .startTime ( LocalTime.now ( ).minusHours ( 2 ) )
                        .endTime ( LocalTime.now ( ).minusHours ( 1 ) )
                        .date ( LocalDate.now ( ) )
                        .physician ( buildPhysician ( ) )
                        .build ( )
        );

        List<TimeSlot> timeSlots = timeSlotRepository.saveAll ( buildTimeSlots ( dailySchedule ) );

        timeSlotService.checkForPassedTimeSlots ( );

        for ( TimeSlot timeSlot : timeSlots ) {
            assertEquals ( Status.PASSED, timeSlotRepository.findById ( timeSlot.getId ( ) ).get ( ).getStatus ( ) );
        }
    }

    @Test
    void when_makeAppointment_withPassedTimeSlots_shouldReplaceStatusToPassedAndThrowException () {
        DailySchedule dailySchedule = dailyScheduleRepository.save (
                DailySchedule.builder ( )
                        .startTime ( LocalTime.now ( ).minusHours ( 2 ) )
                        .endTime ( LocalTime.now ( ).minusHours ( 1 ) )
                        .date ( LocalDate.now ( ) )
                        .physician ( buildPhysician ( ) )
                        .build ( )
        );

        List<TimeSlot> timeSlots = timeSlotRepository.saveAll ( buildTimeSlots ( dailySchedule ) );

        assertThrows ( ScheduleConflictException.class,
                () -> timeSlotService.makeAppointment ( UUID.randomUUID ( ), timeSlots.getLast ( ).getId ( ) )
        );

        TimeSlot timeSlot = timeSlotRepository.findById ( timeSlots.getLast ( ).getId ( ) ).get ( );

        assertEquals ( Status.PASSED, timeSlot.getStatus ( ) );
    }

    @Test
    @Transactional
    void when_makeAppointment_withAvailableTimeslot_shouldMakeAppointment () {
        DailySchedule dailySchedule = dailyScheduleRepository.save (
                DailySchedule.builder ( )
                        .startTime ( LocalTime.now ( ).plusMinutes ( 30 ) )
                        .endTime ( LocalTime.now ( ).plusHours ( 1 ).plusMinutes ( 30 ) )
                        .date ( LocalDate.now ( ) )
                        .physician ( buildPhysician ( ) )
                        .build ( )
        );

        TimeSlot timeSlot = timeSlotRepository.saveAll ( buildTimeSlots ( dailySchedule ) ).getFirst ( );

        UUID accountId = buildPatient ( );
        timeSlotService.makeAppointment ( accountId, timeSlot.getId ( ) );

        Patient patient = patientRepository.findByUserAccount_Id ( accountId ).get ( );
        TimeSlot timeSlotReserved = timeSlotRepository.findById ( timeSlot.getId ( ) ).get ( );

        assertEquals ( Status.RESERVED, timeSlotReserved.getStatus ( ) );
        assertEquals ( patient, timeSlotReserved.getPatient ( ) );
    }

    @Test
    void when_releaseAppointment_withValidRequest_shouldReleaseAppointment () {
        DailySchedule dailySchedule = dailyScheduleRepository.save (
                DailySchedule.builder ( )
                        .startTime ( LocalTime.now ( ).plusMinutes ( 30 ) )
                        .endTime ( LocalTime.now ( ).plusHours ( 1 ).plusMinutes ( 30 ) )
                        .date ( LocalDate.now ( ) )
                        .physician ( buildPhysician ( ) )
                        .build ( )
        );

        TimeSlot timeSlot = timeSlotRepository.saveAll ( buildTimeSlots ( dailySchedule ) ).getFirst ( );
        UUID accountId = buildPatient ( );

        timeSlot.setStatus ( Status.RESERVED );
        timeSlot.setPatient ( patientRepository.findByUserAccount_Id ( accountId ).get ( ) );
        timeSlotRepository.save ( timeSlot );

        timeSlotService.releaseAppointment ( accountId, timeSlot.getId ( ) );

        assertEquals ( Status.FREE, timeSlotRepository.findById ( timeSlot.getId ( ) ).get ( ).getStatus ( ) );
        assertNull ( timeSlotRepository.findById ( timeSlot.getId ( ) ).get ( ).getPatient ( ) );
    }

    private UUID buildPatient () {
        UserAccount userAccount = userAccountRepository.save ( UserAccount.builder ( )
                .email ( "Some@mail" ).role ( Role.ADMIN ).password ( "Some123!" ).status ( UserStatus.ACTIVE ).build ( )
        );


        patientRepository.save ( Patient.builder ( )
                .email ( "Some@mail" ).firstName ( "Some" ).lastName ( "One" ).userAccount ( userAccount )
                .build ( )
        );

        return userAccount.getId ( );
    }

    private List<TimeSlot> buildTimeSlots ( DailySchedule dailySchedule ) {
        return List.of (
                TimeSlot.builder ( )
                        .startTime ( dailySchedule.getStartTime ( ) )
                        .status ( Status.FREE )
                        .durationInMinutes ( 30 )
                        .dailySchedule ( dailySchedule )
                        .build ( ),
                TimeSlot.builder ( )
                        .startTime ( dailySchedule.getStartTime ( ).plusMinutes ( 30 ) )
                        .status ( Status.FREE )
                        .durationInMinutes ( 30 )
                        .dailySchedule ( dailySchedule )
                        .build ( )

        );
    }

    private Physician buildPhysician () {
        Specialty specialty = specialtyRepository.getByName ( SpecialtyName.ALLERGIST ).get ( );

        return physicianRepository.save (
                Physician.builder ( )
                        .email ( "test@test.test" )
                        .firstName ( "Test" )
                        .lastName ( "Test" )
                        .identificationNumber ( "A11111111111" )
                        .specialty ( specialty )
                        .build ( )
        );
    }
}

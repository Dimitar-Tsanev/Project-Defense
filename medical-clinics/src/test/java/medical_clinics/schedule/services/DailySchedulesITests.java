package medical_clinics.schedule.services;

import medical_clinics.clinic.models.Clinic;
import medical_clinics.clinic.models.DaysOfWeek;
import medical_clinics.clinic.models.WorkDay;
import medical_clinics.clinic.repositories.ClinicRepository;
import medical_clinics.clinic.repositories.WorkDaysRepository;
import medical_clinics.physician.model.Physician;
import medical_clinics.physician.repository.PhysicianRepository;
import medical_clinics.schedule.models.DailySchedule;
import medical_clinics.schedule.models.Status;
import medical_clinics.schedule.models.TimeSlot;
import medical_clinics.schedule.repositories.ArchivedSchedulesRepository;
import medical_clinics.schedule.repositories.DailyScheduleRepository;
import medical_clinics.schedule.repositories.TimeSlotRepository;
import medical_clinics.specialty.model.Specialty;
import medical_clinics.specialty.model.SpecialtyName;
import medical_clinics.specialty.repository.SpecialtyRepository;
import medical_clinics.web.dto.NewDaySchedule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class DailySchedulesITests {

    @Autowired
    ArchivedSchedulesRepository archivedSchedulesRepository;

    @Autowired
    DailyScheduleRepository dailyScheduleRepository;

    @Autowired
    TimeSlotRepository timeSlotRepository;

    @Autowired
    WorkDaysRepository workDaysRepository;

    @Autowired
    DailyScheduleService dailyScheduleService;

    @Autowired
    SpecialtyRepository specialtyRepository;

    @Autowired
    PhysicianRepository physicianRepository;

    @Autowired
    ClinicRepository clinicRepository;

    @Test
    void when_archiveSchedules_thenArchivePassedSchedules () {
        LocalTime startTime = LocalTime.of ( 10, 0 );
        LocalTime endTime = LocalTime.of ( 11, 0 );
        LocalDate date = LocalDate.now ( ).plusDays ( 1 );

        Physician physician = buildPhysician ( date, startTime, endTime );

        List<DailySchedule> dailySchedules = List.of (
                DailySchedule.builder ( )
                        .startTime ( startTime )
                        .endTime ( endTime )
                        .date ( LocalDate.now ( ).minusDays ( 6 ) )
                        .physician ( physician )
                        .build ( ),
                DailySchedule.builder ( )
                        .startTime ( LocalTime.of ( 10, 0 ) )
                        .endTime ( LocalTime.of ( 11, 0 ) )
                        .date ( LocalDate.now ( ).minusDays ( 12 ) )
                        .physician ( physician )
                        .build ( ),
                DailySchedule.builder ( )
                        .startTime ( LocalTime.of ( 10, 0 ) )
                        .endTime ( LocalTime.of ( 11, 0 ) )
                        .date ( LocalDate.now ( ).plusDays ( 1 ) )
                        .physician ( physician )
                        .build ( )
        );

        for ( DailySchedule dailySchedule : dailyScheduleRepository.saveAll ( dailySchedules ) ) {
            buildTimeslots ( dailySchedule );
        }

        List<TimeSlot> timeSlots = timeSlotRepository.findAll ( );
        List<DailySchedule> dailySchedulesSaved = dailyScheduleRepository.saveAll ( dailySchedules );

        dailyScheduleService.archiveSchedules ( );

        assertEquals ( dailySchedules.size ( ), dailySchedulesSaved.size ( ) );
        assertEquals ( dailySchedules.size ( ) * 2, timeSlots.size ( ) );

        assertEquals ( 1, dailyScheduleRepository.count ( ) );
        assertEquals ( 2, timeSlotRepository.count ( ) );

        assertEquals ( archivedSchedulesRepository.count ( ), (dailySchedules.size ( ) - 1) * 2 );
    }

    @Test
    void when_generateDaySchedule_withNoScheduleForTheDay_thenGenerateDaySchedule () {
        LocalTime startTime = LocalTime.of ( 10, 0 );
        LocalTime endTime = LocalTime.of ( 11, 0 );
        LocalDate date = LocalDate.now ( ).plusDays ( 1 );

        Physician physician = buildPhysician ( date, startTime, endTime );

        NewDaySchedule newDaySchedule = NewDaySchedule.builder ( )
                .startTime ( startTime )
                .endTime ( endTime )
                .date ( date )
                .timeSlotInterval ( 30 )
                .build ( );

        dailyScheduleService.generateDaySchedule ( physician, newDaySchedule );

        assertEquals ( 1, dailyScheduleRepository.count ( ) );
        assertEquals ( 2, timeSlotRepository.count ( ) );
    }

    @Test
    void when_generateDaySchedule_withExistingScheduleBeforeTheNewOne_thenUpdateDaySchedule () {
        LocalTime startTime = LocalTime.of ( 10, 0 );
        LocalTime endTime = LocalTime.of ( 12, 0 );
        LocalDate date = LocalDate.now ( ).plusDays ( 1 );

        Physician physician = buildPhysician ( date, startTime, endTime );

        DailySchedule dailySchedules = DailySchedule.builder ( )
                .startTime ( startTime )
                .endTime ( LocalTime.of ( 11, 0 ) )
                .date ( date )
                .physician ( physician )
                .build ( );

        dailyScheduleRepository.save ( dailySchedules );
        buildTimeslots ( dailySchedules );

        NewDaySchedule newDaySchedule = NewDaySchedule.builder ( )
                .startTime ( LocalTime.of ( 11, 0 ) )
                .endTime ( endTime )
                .date ( date )
                .timeSlotInterval ( 30 )
                .build ( );

        dailyScheduleService.generateDaySchedule ( physician, newDaySchedule );

        DailySchedule afterUpdate = dailyScheduleRepository.findById ( dailySchedules.getId ( ) ).get ( );

        assertEquals ( 1, dailyScheduleRepository.count ( ) );
        assertEquals ( 4, afterUpdate.getTimeSlots ( ).size ( ) );
        assertEquals ( startTime, afterUpdate.getStartTime ( ) );
        assertEquals ( endTime, afterUpdate.getEndTime ( ) );
    }

    @Test
    void when_generateDaySchedule_withExistingScheduleAfterTheNewOne_thenUpdateDaySchedule () {
        LocalTime startTime = LocalTime.of ( 10, 0 );
        LocalTime endTime = LocalTime.of ( 12, 0 );
        LocalDate date = LocalDate.now ( ).plusDays ( 1 );

        Physician physician = buildPhysician ( date, startTime, endTime );

        DailySchedule dailySchedules = DailySchedule.builder ( )
                .startTime ( LocalTime.of ( 11, 0 ) )
                .endTime ( endTime )
                .date ( date )
                .physician ( physician )
                .build ( );

        dailyScheduleRepository.save ( dailySchedules );
        buildTimeslots ( dailySchedules );

        NewDaySchedule newDaySchedule = NewDaySchedule.builder ( )
                .startTime ( startTime )
                .endTime ( LocalTime.of ( 11, 0 ) )
                .date ( date )
                .timeSlotInterval ( 30 )
                .build ( );

        dailyScheduleService.generateDaySchedule ( physician, newDaySchedule );

        DailySchedule afterUpdate = dailyScheduleRepository.findById ( dailySchedules.getId ( ) ).get ( );

        assertEquals ( 1, dailyScheduleRepository.count ( ) );
        assertEquals ( 4, afterUpdate.getTimeSlots ( ).size ( ) );
        assertEquals ( startTime, afterUpdate.getStartTime ( ) );
        assertEquals ( endTime, afterUpdate.getEndTime ( ) );
    }

    @Test
    void when_generateDaySchedule_withExistingScheduleIsInTheNewOne_thenUpdateDaySchedule () {
        LocalTime startTime = LocalTime.of ( 10, 0 );
        LocalTime endTime = LocalTime.of ( 13, 0 );
        LocalDate date = LocalDate.now ( ).plusDays ( 1 );

        Physician physician = buildPhysician ( date, startTime, endTime );

        DailySchedule dailySchedules = DailySchedule.builder ( )
                .startTime ( LocalTime.of ( 11, 0 ) )
                .endTime ( LocalTime.of ( 12, 0 ) )
                .date ( date )
                .physician ( physician )
                .build ( );

        dailyScheduleRepository.save ( dailySchedules );
        buildTimeslots ( dailySchedules );

        NewDaySchedule newDaySchedule = NewDaySchedule.builder ( )
                .startTime ( startTime )
                .endTime ( endTime )
                .date ( date )
                .timeSlotInterval ( 30 )
                .build ( );

        dailyScheduleService.generateDaySchedule ( physician, newDaySchedule );

        DailySchedule afterUpdate = dailyScheduleRepository.findById ( dailySchedules.getId ( ) ).get ( );

        assertEquals ( 1, dailyScheduleRepository.count ( ) );
        assertEquals ( 6, afterUpdate.getTimeSlots ( ).size ( ) );
        assertEquals ( startTime, afterUpdate.getStartTime ( ) );
        assertEquals ( endTime, afterUpdate.getEndTime ( ) );
    }

    @Test
    void when_generateDaySchedule_withExistingScheduleSameAsNewOne_thenNothingHappened () {
        LocalTime startTime = LocalTime.of ( 10, 0 );
        LocalTime endTime = LocalTime.of ( 11, 0 );
        LocalDate date = LocalDate.now ( ).plusDays ( 1 );

        Physician physician = buildPhysician ( date, startTime, endTime );

        DailySchedule dailySchedules = DailySchedule.builder ( )
                .startTime ( startTime )
                .endTime ( endTime )
                .date ( date )
                .physician ( physician )
                .build ( );

        dailyScheduleRepository.save ( dailySchedules );
        buildTimeslots ( dailySchedules );

        NewDaySchedule newDaySchedule = NewDaySchedule.builder ( )
                .startTime ( startTime )
                .endTime ( endTime )
                .date ( date )
                .timeSlotInterval ( 30 )
                .build ( );

        DailySchedule beforeUpdate = dailyScheduleRepository.findById ( dailySchedules.getId ( ) ).get ( );

        dailyScheduleService.generateDaySchedule ( physician, newDaySchedule );

        DailySchedule afterUpdate = dailyScheduleRepository.findById ( dailySchedules.getId ( ) ).get ( );

        assertEquals ( 1, dailyScheduleRepository.count ( ) );
        assertEquals ( 2, beforeUpdate.getTimeSlots ( ).size ( ) );
        assertEquals ( 2, afterUpdate.getTimeSlots ( ).size ( ) );
        assertEquals ( startTime, beforeUpdate.getStartTime ( ) );
        assertEquals ( endTime, beforeUpdate.getEndTime ( ) );
        assertEquals ( beforeUpdate.getStartTime ( ), afterUpdate.getStartTime () );
        assertEquals ( beforeUpdate.getEndTime (), afterUpdate.getEndTime () );
    }

    private void buildTimeslots ( DailySchedule dailySchedule ) {
        timeSlotRepository.saveAll (
                List.of (
                        TimeSlot.builder ( )
                                .startTime ( LocalTime.of ( 10, 0 ) )
                                .dailySchedule ( dailySchedule )
                                .durationInMinutes ( 30 )
                                .status ( Status.FREE )
                                .build ( ),
                        TimeSlot.builder ( )
                                .startTime ( LocalTime.of ( 10, 30 ) )
                                .dailySchedule ( dailySchedule )
                                .durationInMinutes ( 30 )
                                .status ( Status.FREE )
                                .build ( )
                )
        );
    }

    private Physician buildPhysician ( LocalDate date, LocalTime startTime, LocalTime endTime ) {
        Specialty specialty = specialtyRepository.getByName ( SpecialtyName.ALLERGIST ).get ( );

        Clinic clinic = clinicRepository.save (
                Clinic.builder ( )
                        .city ( "Somewhere" )
                        .address ( "Somewhere" )
                        .description ( "some" )
                        .pictureUrl ( "https://somewhere.com" )
                        .phoneNumber ( "123456789" )
                        .identificationNumber ( "123456789" )
                        .build ( )
        );

        workDaysRepository.saveAll ( List.of ( WorkDay.builder ( )
                .dayOfWeek ( DaysOfWeek.valueOf (
                        date.getDayOfWeek ( ).toString ( ) )
                )
                .startOfWorkingDay ( startTime )
                .endOfWorkingDay ( endTime )
                .clinic ( clinic )
                .build ( ) ) );

        return physicianRepository.save (
                Physician.builder ( )
                        .email ( "test@test.test" )
                        .firstName ( "Test" )
                        .lastName ( "Test" )
                        .identificationNumber ( "A11111111111" )
                        .specialty ( specialty )
                        .workplace ( clinicRepository.findById ( clinic.getId ( ) ).get ( ) )
                        .build ( )
        );
    }
}

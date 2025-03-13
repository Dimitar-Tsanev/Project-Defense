package medical_clinics.schedule.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import medical_clinics.clinic.models.Clinic;
import medical_clinics.clinic.models.DaysOfWeek;
import medical_clinics.clinic.models.WorkDay;
import medical_clinics.physician.model.Physician;
import medical_clinics.schedule.exceptions.ScheduleConflictException;
import medical_clinics.schedule.exceptions.ScheduleNotFoundException;
import medical_clinics.schedule.mapper.DailyScheduleMapper;
import medical_clinics.schedule.models.DailySchedule;
import medical_clinics.schedule.models.TimeSlot;
import medical_clinics.schedule.repositories.ArchivedSchedulesRepository;
import medical_clinics.schedule.repositories.DailyScheduleRepository;
import medical_clinics.web.dto.NewDaySchedule;
import medical_clinics.web.dto.response.PhysicianDaySchedule;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class DailyScheduleService {
    private final DailyScheduleRepository dailyScheduleRepository;
    private final TimeSlotService timeSlotService;
    private final ArchivedSchedulesRepository archivedSchedulesRepository;

    @Transactional
    public void generateDaySchedule ( Physician physician, NewDaySchedule newDaySchedule ) {
        DailySchedule dailySchedule = DailyScheduleMapper.mapToDailySchedule ( newDaySchedule );

        Clinic physicianWorkplace = physician.getWorkplace ( );

        WorkDay workDay = getWorkDayOfClinic (
                physicianWorkplace,
                dailySchedule.getDate ( )
        );

        checkScheduleStartTimeIncorrect ( workDay, dailySchedule.getStartTime ( ) );
        isScheduleEndTimeIncorrect ( workDay, dailySchedule.getEndTime ( ) );

        Collection<TimeSlot> timeSlots = timeSlotService.generateTimeSlots (
                dailySchedule.getStartTime ( ),
                dailySchedule.getEndTime ( ),
                newDaySchedule.getTimeSlotInterval ( )
        );

        dailySchedule.setTimeSlots ( timeSlots );
        dailySchedule.setPhysician ( physician );

        dailyScheduleRepository.save ( dailySchedule );
    }

    @Transactional
    public void inactivateDaySchedule ( UUID physicianId, LocalDate localDate ) {
        Optional<DailySchedule> schedule = dailyScheduleRepository.findByPhysicianIdAndDate ( physicianId, localDate );

        if ( schedule.isEmpty ( ) ) {
            throw new ScheduleNotFoundException ( "Cant find schedule on date [%s] for physician [%s]".formatted (
                    localDate.format ( DateTimeFormatter.ofPattern ( "dd MM yyyy" ) ), physicianId ) );
        }

        schedule.get ( ).getTimeSlots ( ).forEach ( t -> timeSlotService.inactivate ( t.getId ( ) ) );
    }

    public List<PhysicianDaySchedule> getPublicPhysicianSchedules ( UUID physicianId ) {
        List<DailySchedule> dailySchedules = dailyScheduleRepository.findAllByPhysicianId ( physicianId );
        return dailySchedules.stream ( ).map ( DailyScheduleMapper::mapToPublicResponse ).toList ( );
    }

    public List<PhysicianDaySchedule> getPrivatePhysicianSchedules ( UUID physicianId ) {
        List<DailySchedule> dailySchedules = dailyScheduleRepository.findAllByPhysicianId ( physicianId );
        return dailySchedules.stream ( ).map ( DailyScheduleMapper::mapToPrivateResponse ).toList ( );
    }

    public void deletePhysicianFutureSchedules ( Physician physician ) {
        List<DailySchedule> schedules = dailyScheduleRepository.findAllByPhysicianIdAndDateAfter (
                physician.getId ( ), LocalDate.now ( )
        );

        for ( DailySchedule schedule : schedules ) {
            schedule.getTimeSlots ( ).forEach ( timeSlot -> {
                archivedSchedulesRepository.save ( DailyScheduleMapper.mapToArchive ( timeSlot ) );
                timeSlotService.delete ( timeSlot );
            } );
            dailyScheduleRepository.delete ( schedule );
        }
    }

    @Scheduled(cron = "0 00 00 * * *")
    @Transactional
    void archiveSchedules () {
        List<DailySchedule> dailySchedules = dailyScheduleRepository.findAllByDateBefore ( LocalDate.now ( ) );

        for ( DailySchedule dailySchedule : dailySchedules ) {
            dailySchedule.getTimeSlots ( ).forEach ( timeSlot -> {
                archivedSchedulesRepository.save ( DailyScheduleMapper.mapToArchive ( timeSlot ) );
                timeSlotService.delete ( timeSlot );
            } );
            dailyScheduleRepository.delete ( dailySchedule );
        }
    }

    private WorkDay getWorkDayOfClinic ( Clinic clinic, LocalDate scheduleDate ) {
        String day = scheduleDate.getDayOfWeek ( ).name ( ).toUpperCase ( );
        DaysOfWeek dayOfWeek = DaysOfWeek.valueOf ( day );

        Collection<WorkDay> clinicWorkdays = clinic.getWorkingDays ( );

        Optional<WorkDay> workDayOptional = Optional.empty ( );

        for ( WorkDay workDay : clinicWorkdays ) {
            if ( workDay.getDayOfWeek ( ).equals ( dayOfWeek ) ) {
                workDayOptional = Optional.of ( workDay );
                break;
            }
        }

        if ( workDayOptional.isEmpty ( ) ) {
            throw new ScheduleConflictException ( "Schedule day dose not match work day of the Clinic" );
        }

        return workDayOptional.get ( );
    }

    private void checkScheduleStartTimeIncorrect ( WorkDay workDay, LocalTime scheduleStartTime ) {
        LocalTime clinicBeginOfWorkday = workDay.getStartOfWorkingDay ( );
        if ( scheduleStartTime.isBefore ( clinicBeginOfWorkday ) ) {
            throw new ScheduleConflictException ( "Schedule start time is before clinic work day start" );
        }
    }

    private void isScheduleEndTimeIncorrect ( WorkDay workDay, LocalTime scheduleEndTime ) {
        LocalTime clinicEndOfWorkday = workDay.getEndOfWorkingDay ( );
        if ( scheduleEndTime.isAfter ( clinicEndOfWorkday ) ) {
            throw new ScheduleConflictException ( "Schedule end time is after clinic work day end" );
        }
    }
}

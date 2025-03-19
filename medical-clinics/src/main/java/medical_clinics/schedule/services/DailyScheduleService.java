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
import medical_clinics.web.dto.response.schedule_private.PhysicianDaySchedulePrivate;
import medical_clinics.web.dto.response.schedule_public.PhysicianDaySchedulePublic;
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
        if ( !newDaySchedule.getStartTime ( ).isBefore ( newDaySchedule.getEndTime ( ) ) ) {
            throw new ScheduleConflictException ( "Start time of schedule must be before end time" );
        }

        Clinic physicianWorkplace = physician.getWorkplace ( );

        WorkDay workDay = getWorkDayOfClinic (
                physicianWorkplace,
                newDaySchedule.getDate ( )
        );

        checkScheduleStartTimeIncorrect ( workDay, newDaySchedule.getStartTime ( ) );
        isScheduleEndTimeIncorrect ( workDay, newDaySchedule.getEndTime ( ) );

        Optional<DailySchedule> existingDayScheduleOptional = dailyScheduleRepository.findByPhysicianIdAndDate (
                physician.getId ( ), newDaySchedule.getDate ( )
        );

        if ( existingDayScheduleOptional.isEmpty ( ) ) {
            createSchedule ( physician, newDaySchedule );
            return;
        }

        DailySchedule existingDaySchedule = existingDayScheduleOptional.get ( );

        if ( checkIsForUpdate ( existingDaySchedule, newDaySchedule ) ) {
            updateSchedule ( existingDaySchedule, newDaySchedule );
        }
    }

    @Transactional
    public void inactivateDaySchedule ( UUID physicianId, LocalDate localDate ) {
        Optional<DailySchedule> schedule = dailyScheduleRepository.findAllByPhysician_UserAccount_IdAndDate (
                physicianId, localDate
        );

        if ( schedule.isEmpty ( ) ) {
            throw new ScheduleNotFoundException ( "Cant find schedule on date [%s] for physician [%s]".formatted (
                    localDate.format ( DateTimeFormatter.ofPattern ( "dd MM yyyy" ) ), physicianId ) );
        }

        schedule.get ( ).getTimeSlots ( ).forEach ( t -> timeSlotService.inactivate ( t.getId ( ) ) );
    }

    public List<PhysicianDaySchedulePublic> getPublicPhysicianSchedules ( UUID physicianId ) {
        List<DailySchedule> dailySchedules = dailyScheduleRepository.findAllByPhysicianIdOrderByDateAsc ( physicianId );
        return dailySchedules.stream ( ).map ( DailyScheduleMapper::mapToPublicResponse ).toList ( );
    }

    public List<PhysicianDaySchedulePrivate> getPrivatePhysicianSchedules ( UUID physicianId ) {
        List<DailySchedule> dailySchedules =
                dailyScheduleRepository.findAllByPhysician_UserAccount_IdOrderByDateAsc ( physicianId );

        return dailySchedules.stream ( ).map ( DailyScheduleMapper::mapToPrivateResponse ).toList ( );
    }

    public void deletePhysicianFutureSchedules ( Physician physician ) {
        List<DailySchedule> schedules = dailyScheduleRepository.findAllByPhysician_IdAndDateAfter (
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

    private void createSchedule ( Physician physician, NewDaySchedule newDaySchedule ) {
        DailySchedule dailySchedule = DailyScheduleMapper.mapToDailySchedule ( newDaySchedule );

        dailySchedule.setPhysician ( physician );

        DailySchedule schedule = dailyScheduleRepository.save ( dailySchedule );

        Collection<TimeSlot> timeSlots = timeSlotService.generateTimeSlots (
                dailySchedule.getStartTime ( ),
                dailySchedule.getEndTime ( ),
                newDaySchedule.getTimeSlotInterval ( ),
                schedule
        );

        schedule.setTimeSlots ( timeSlots );
    }

    private boolean checkIsForUpdate ( DailySchedule existingDaySchedule, NewDaySchedule newDaySchedule ) {
        LocalTime existingScheduleStartTime = existingDaySchedule.getStartTime ( );
        LocalTime existingScheduleEndTime = existingDaySchedule.getEndTime ( );
        LocalTime newScheduleStartTime = newDaySchedule.getStartTime ( );
        LocalTime newScheduleEndTime = newDaySchedule.getEndTime ( );

        boolean isMatchExistingSchedule = existingScheduleStartTime.equals ( newScheduleStartTime ) &&
                existingScheduleEndTime.equals ( newScheduleEndTime );

        boolean isInExistingSchedule = existingScheduleStartTime.isBefore ( newScheduleStartTime ) &&
                existingScheduleEndTime.isAfter ( newScheduleEndTime );

        return !isMatchExistingSchedule && !isInExistingSchedule;
    }

    private void updateSchedule ( DailySchedule dailySchedule, NewDaySchedule newDaySchedule ) {
        int interval = newDaySchedule.getTimeSlotInterval ( );

        LocalTime existingScheduleStartTime = dailySchedule.getStartTime ( );
        LocalTime existingScheduleEndTime = dailySchedule.getEndTime ( );

        LocalTime newScheduleStartTime = newDaySchedule.getStartTime ( );
        LocalTime newScheduleEndTime = newDaySchedule.getEndTime ( );

        boolean isAfterExistingSchedule = !existingScheduleEndTime.isAfter ( newScheduleStartTime );
        boolean isBeforeExistingSchedule = !existingScheduleStartTime.isBefore ( newScheduleEndTime );

        boolean isStartInBetweenExistingSchedule = !existingScheduleStartTime.isAfter ( newScheduleStartTime ) &&
                existingScheduleEndTime.isAfter ( newScheduleStartTime );

        boolean isEndInBetweenExistingSchedule = existingScheduleStartTime.isBefore ( newScheduleEndTime ) &&
                !existingScheduleEndTime.isBefore ( newScheduleEndTime );

        boolean isNewScheduleLarger = existingScheduleStartTime.isAfter ( newScheduleStartTime ) &&
                existingScheduleEndTime.isBefore ( newScheduleEndTime );

        if ( isAfterExistingSchedule ) {
            dailySchedule.setEndTime ( newScheduleEndTime );

            timeSlotService.generateTimeSlots ( newScheduleStartTime, newScheduleEndTime, interval, dailySchedule );
        }

        if ( isBeforeExistingSchedule ) {
            dailySchedule.setStartTime ( newScheduleStartTime );
            timeSlotService.generateTimeSlots ( newScheduleStartTime, newScheduleEndTime, interval, dailySchedule );
        }

        if ( isStartInBetweenExistingSchedule || isNewScheduleLarger ) {
            dailySchedule.setEndTime ( newScheduleEndTime );
            timeSlotService.generateTimeSlots ( existingScheduleEndTime, newScheduleEndTime, interval, dailySchedule );
        }

        if ( isEndInBetweenExistingSchedule || isNewScheduleLarger ) {
            dailySchedule.setStartTime ( newScheduleStartTime );
            timeSlotService.generateTimeSlots ( newScheduleStartTime, existingScheduleStartTime, interval, dailySchedule );
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

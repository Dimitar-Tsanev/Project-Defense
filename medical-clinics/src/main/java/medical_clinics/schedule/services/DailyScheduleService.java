package medical_clinics.schedule.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import medical_clinics.clinic.models.Clinic;
import medical_clinics.clinic.models.DaysOfWeek;
import medical_clinics.clinic.models.WorkDay;
import medical_clinics.physician.model.Physician;
import medical_clinics.schedule.exceptions.ScheduleConflictException;
import medical_clinics.schedule.mapper.DailyScheduleMapper;
import medical_clinics.schedule.models.DailySchedule;
import medical_clinics.schedule.models.TimeSlot;
import medical_clinics.schedule.repositories.DailyScheduleRepository;
import medical_clinics.web.dto.DailyScheduleDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DailyScheduleService {
    private final DailyScheduleRepository dailyScheduleRepository;
    private final TimeSlotService timeSlotService;

    @Transactional
    public void generateDaySchedule ( Physician physician, DailyScheduleDto dailyScheduleDto ) {
        DailySchedule dailySchedule = DailyScheduleMapper.mapToDailySchedule ( dailyScheduleDto );

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
                dailyScheduleDto.getTimeSlotInterval ( )
        );

        dailySchedule.setTimeSlots ( timeSlots );
        dailySchedule.setPhysician ( physician );

        dailyScheduleRepository.save ( dailySchedule );
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

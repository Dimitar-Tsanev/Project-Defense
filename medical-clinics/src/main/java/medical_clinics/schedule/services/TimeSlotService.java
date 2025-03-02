package medical_clinics.schedule.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import medical_clinics.patient.model.Patient;
import medical_clinics.patient.service.PatientService;
import medical_clinics.schedule.models.Status;
import medical_clinics.schedule.models.TimeSlot;
import medical_clinics.schedule.repositories.TimeSlotRepository;
import medical_clinics.shared.exception.ScheduleException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TimeSlotService {
    private final TimeSlotRepository timeSlotRepository;
    private final PatientService patientService;

    Collection<TimeSlot> generateTimeSlots (
            LocalTime scheduleStartTime,
            LocalTime scheduleEndTime,
            Integer timeSlotInterval ) {

        List<TimeSlot> timeSlots = new ArrayList<> ( );
        LocalTime startTime = scheduleStartTime;

        while (!startTime.isAfter ( scheduleEndTime )) {
            TimeSlot timeSlot = TimeSlot.builder ( )
                    .startTime ( startTime )
                    .durationInMinutes ( timeSlotInterval )
                    .status ( Status.FREE )
                    .build ( );

            timeSlots.add ( timeSlot );
            startTime = startTime.plusMinutes ( timeSlotInterval );
        }
        return timeSlotRepository.saveAll ( timeSlots );
    }

    @Transactional
    public void makeAppointment ( UUID accountId, UUID timeSlotId ) {
        TimeSlot timeSlot = getIfExist ( timeSlotId );

        boolean errorFlag = false;

        if ( isPassed ( timeSlot ) ) {
            timeSlot.setStatus ( Status.PASSED );
        }

        if ( !timeSlot.getStatus ( ).equals ( Status.FREE ) ) {
            errorFlag = true;
        }

        if ( timeSlot.getStatus ( ).equals ( Status.FREE ) ) {
            Patient patient = patientService.getPatientByUserAccountId ( accountId );

            timeSlot.setPatient ( patient );
            timeSlot.setStatus ( Status.RESERVED );
        }

        timeSlotRepository.save ( timeSlot );

        if ( errorFlag ) {
            throw new ScheduleException ( "The appointment hour you are trying to preserve is not available." );
        }
    }

    @Scheduled(cron = "0 */15 06-22 * * *")
    void checkForPassedTimeSlots () {
        List<TimeSlot> passedFreeTimeSlots = timeSlotRepository
                .findAllByStatusEqualsAndStartTimeBeforeAndDailySchedule_Date (
                        Status.FREE, LocalTime.now ( ), LocalDate.now ( )
                );

        for ( TimeSlot timeSlot : passedFreeTimeSlots ) {
            timeSlot.setStatus ( Status.PASSED );
        }

        timeSlotRepository.saveAll ( passedFreeTimeSlots );
    }

    private TimeSlot getIfExist ( UUID timeSlotId ) {
        return timeSlotRepository.findById ( timeSlotId ).orElseThrow ( () ->
                new ScheduleException ( "The appointment hour you are trying to preserve does not exist." )
        );
    }

    private boolean isPassed ( TimeSlot timeSlot ) {
        LocalDate currentDate = LocalDate.now ( );
        LocalTime currentTime = LocalTime.now ( );

        LocalDate appointmentDate = timeSlot.getDailySchedule ( ).getDate ( );
        LocalTime appointmentStartTime = timeSlot.getStartTime ( );

        if ( !currentDate.isAfter ( appointmentDate ) ) {
            return true;
        }

        return !currentTime.isAfter ( appointmentStartTime );
    }
}

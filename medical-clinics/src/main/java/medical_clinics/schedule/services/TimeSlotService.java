package medical_clinics.schedule.services;

import lombok.AllArgsConstructor;
import medical_clinics.patient.model.Patient;
import medical_clinics.patient.service.PatientService;
import medical_clinics.schedule.exceptions.ScheduleConflictException;
import medical_clinics.schedule.exceptions.ScheduleNotFoundException;
import medical_clinics.schedule.mapper.DailyScheduleMapper;
import medical_clinics.schedule.models.DailySchedule;
import medical_clinics.schedule.models.Status;
import medical_clinics.schedule.models.TimeSlot;
import medical_clinics.schedule.repositories.TimeSlotRepository;
import medical_clinics.web.dto.response.PatientAppointment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@AllArgsConstructor
public class TimeSlotService {
    private final TimeSlotRepository timeSlotRepository;
    private final PatientService patientService;

    public void inactivate ( UUID timeSlotId ) {
        Optional<TimeSlot> timeSlotOptional = timeSlotRepository.findById ( timeSlotId );

        if ( timeSlotOptional.isEmpty ( ) ) {
            throw new ScheduleNotFoundException ( "TimeSlot with id " + timeSlotId + " not found" );
        }

        TimeSlot timeSlot = timeSlotOptional.get ( );

        if ( timeSlot.getPatient ( ) != null ) {
            String names = timeSlot.getPatient ( ).getFirstName ( ) + " " + timeSlot.getPatient ( ).getLastName ( );
            String email = timeSlot.getPatient ( ).getEmail ( );
            String phone = timeSlot.getPatient ( ).getPhone ( );
            String patientFormated = names + ", contacts:  " + email + ", " + phone;

            String date = timeSlot.getDailySchedule ( ).getDate ( ).format ( DateTimeFormatter.ofPattern ( "dd-MMM-yyyy" ) );
            String time = timeSlot.getStartTime ( ).format ( DateTimeFormatter.ofPattern ( "HH:mm" ) );
            String timeslotDayAndTime = date + ", " + time;

            throw new ScheduleConflictException ( "Timeslot cannot be inactivated because it is reserved." +
                    " Contact the Patient - [%s] to release the appointment then inactivate the timeslot: [%s]"
                            .formatted ( patientFormated, timeslotDayAndTime )
            );
        }

        timeSlot.setStatus ( Status.INACTIVE );
        timeSlotRepository.save ( timeSlot );
    }

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
            throw new ScheduleConflictException ( "The appointment hour you are trying to preserve is not available." );
        }
    }

    public List<PatientAppointment> getPatientAppointments ( UUID patientId ) {
        return timeSlotRepository.findAllByPatient_Id ( patientId )
                .stream ( )
                .map ( DailyScheduleMapper::mapToAppointment )
                .toList ( );
    }

    public void releaseAppointment ( UUID accountId, UUID appointmentId ) {
        TimeSlot timeSlot = getIfExist ( appointmentId );

        if ( !accountId.equals ( timeSlot.getPatient ( ).getUserAccount ( ).getId ( ) ) ) {
            throw new ScheduleConflictException (
                    "The appointment hour you are trying to release not belong to user account: " + accountId
            );
        }

        if ( isPassed ( timeSlot ) ) {
            return;
        }

        timeSlot.setStatus ( Status.FREE );
        timeSlot.setPatient ( null );
        timeSlotRepository.save ( timeSlot );
    }

    Collection<TimeSlot> generateTimeSlots (
            LocalTime scheduleStartTime,
            LocalTime scheduleEndTime,
            Integer timeSlotInterval, DailySchedule schedule ) {

        List<TimeSlot> timeSlots = new ArrayList<> ( );
        LocalTime startTime = scheduleStartTime;

        while (startTime.isBefore ( scheduleEndTime )) {
            TimeSlot timeSlot = TimeSlot.builder ( )
                    .startTime ( startTime )
                    .durationInMinutes ( timeSlotInterval )
                    .status ( Status.FREE )
                    .dailySchedule ( schedule )
                    .build ( );

            timeSlots.add ( timeSlotRepository.save ( timeSlot ) );
            startTime = startTime.plusMinutes ( timeSlotInterval );
        }
        return timeSlots;
    }

    void delete ( TimeSlot timeSlot ) {
        timeSlotRepository.delete ( timeSlot );
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
                new ScheduleNotFoundException ( "The appointment hour you are trying to preserve does not exist." )
        );
    }

    private boolean isPassed ( TimeSlot timeSlot ) {
        LocalDate currentDate = LocalDate.now ( );
        LocalTime currentTime = LocalTime.now ( );

        LocalDate appointmentDate = timeSlot.getDailySchedule ( ).getDate ( );
        LocalTime appointmentStartTime = timeSlot.getStartTime ( );

        if ( appointmentDate.isBefore ( currentDate )){
            return true;
        }

        if ( currentDate.equals ( appointmentDate ) ) {
            return !currentTime.isBefore ( appointmentStartTime );
        }

        return false;
    }
}

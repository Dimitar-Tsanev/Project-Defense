package medical_clinics.schedule.mapper;

import medical_clinics.patient.mapper.PatientMapper;
import medical_clinics.physician.model.Physician;
import medical_clinics.schedule.models.ArchivedSchedules;
import medical_clinics.schedule.models.DailySchedule;
import medical_clinics.schedule.models.TimeSlot;
import medical_clinics.web.dto.NewDaySchedule;
import medical_clinics.web.dto.response.PatientAppointment;
import medical_clinics.web.dto.response.PhysicianDaySchedule;
import medical_clinics.web.dto.response.apointmet.DayAppointment;
import medical_clinics.web.dto.response.apointmet.DayAppointmentPrivate;
import medical_clinics.web.dto.response.apointmet.DayAppointmentPublic;

import java.util.UUID;

public class DailyScheduleMapper {
    private DailyScheduleMapper () {
    }

    public static DailySchedule mapToDailySchedule ( NewDaySchedule newDaySchedule ) {
        return DailySchedule.builder ( )
                .date ( newDaySchedule.getDate ( ) )
                .startTime ( newDaySchedule.getStartTime ( ) )
                .endTime ( newDaySchedule.getEndTime ( ) )
                .build ( );
    }

    public static PhysicianDaySchedule mapToPublicResponse ( DailySchedule schedule ) {
        return PhysicianDaySchedule.builder ( )
                .id ( schedule.getId ( ) )
                .date ( schedule.getDate ( ) )
                .schedule (
                        schedule.getTimeSlots ( ).stream ( )
                                .map ( DailyScheduleMapper::mapToAppointmentPublic )
                                .toList ( )
                )
                .build ( );
    }

    public static PhysicianDaySchedule mapToPrivateResponse ( DailySchedule schedule ) {
        return PhysicianDaySchedule.builder ( )
                .id ( schedule.getId ( ) )
                .date ( schedule.getDate ( ) )
                .schedule (
                        schedule.getTimeSlots ( ).stream ( )
                                .map ( DailyScheduleMapper::mapToAppointmentPrivate )
                                .toList ( )
                )
                .build ( );
    }

    public static PatientAppointment mapToAppointment ( TimeSlot timeSlot ) {
        Physician physician = timeSlot.getDailySchedule ( ).getPhysician ( );

        String physicianNames = physician.getFirstName ( ) + " " + physician.getLastName ( );
        String physicianInfo = physicianNames + ", " +
                physician.getAbbreviation ( ) + ", " +
                physician.getSpecialty ( ).getName ( );

        return PatientAppointment.builder ( )
                .id ( timeSlot.getId ( ) )
                .startTime ( timeSlot.getStartTime ( ) )
                .appointmentDate ( timeSlot.getDailySchedule ( ).getDate ( ) )
                .physician (
                        physicianInfo
                )
                .address (
                        physician.getWorkplace ( ).getCity ( ) + ", " +
                                physician.getWorkplace ( ).getAddress ( )
                )
                .build ( );
    }

    private static DayAppointment mapToAppointmentPublic ( TimeSlot interval ) {
        return DayAppointmentPublic.builder ( )
                .id ( interval.getId ( ) )
                .startTime ( interval.getStartTime ( ) )
                .status ( interval.getStatus ( ) )
                .build ( );
    }

    private static DayAppointment mapToAppointmentPrivate ( TimeSlot interval ) {
        return DayAppointmentPrivate.builder ( )
                .id ( interval.getId ( ) )
                .startTime ( interval.getStartTime ( ) )
                .status ( interval.getStatus ( ) )
                .patientInfo ( PatientMapper.mapToPatientInfo ( interval.getPatient ( ) ) )
                .build ( );
    }

    public static ArchivedSchedules mapToArchive ( TimeSlot timeSlot ) {
        UUID patientId = null;
        if ( timeSlot.getPatient ( ) != null ) {
            patientId = timeSlot.getPatient ( ).getId ( );
        }
        return ArchivedSchedules.builder ( )
                .date ( timeSlot.getDailySchedule ( ).getDate ( ) )
                .startTime ( timeSlot.getStartTime ( ) )
                .status ( timeSlot.getStatus ( ) )
                .physicianId ( timeSlot.getDailySchedule ( ).getPhysician ( ).getId ( ) )
                .patientId ( patientId )
                .durationInMinutes ( timeSlot.getDurationInMinutes ( ) )
                .build ( );
    }
}

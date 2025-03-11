package medical_clinics.schedule.exceptions;

public class ScheduleNotFoundException extends RuntimeException {
    public ScheduleNotFoundException ( String message ) {
        super ( message );
    }
}

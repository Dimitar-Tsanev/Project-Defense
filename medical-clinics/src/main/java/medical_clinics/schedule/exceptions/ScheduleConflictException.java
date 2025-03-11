package medical_clinics.schedule.exceptions;

public class ScheduleConflictException extends IllegalArgumentException {
    public ScheduleConflictException ( String message ) {
        super ( message );
    }
}

package medical_clinics.shared.exception;

public class ScheduleConflictException extends IllegalArgumentException {
    public ScheduleConflictException ( String message ) {
        super ( message );
    }
}

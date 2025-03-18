package medical_clinics.schedule.exceptions;

import java.util.NoSuchElementException;

public class ScheduleNotFoundException extends NoSuchElementException {
    public ScheduleNotFoundException ( String message ) {
        super ( message );
    }
}

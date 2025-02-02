package medical_clinics.shared.exception;

public class PatientDontMatchException extends RuntimeException {
    public PatientDontMatchException ( String message ) {
        super ( message );
    }
}

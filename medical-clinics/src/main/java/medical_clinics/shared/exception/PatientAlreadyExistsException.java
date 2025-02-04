package medical_clinics.shared.exception;

public class PatientAlreadyExistsException extends IllegalArgumentException {
    public PatientAlreadyExistsException ( String message ) {
        super ( message );
    }
}

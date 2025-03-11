package medical_clinics.patient.exceptions;

public class PatientAlreadyExistsException extends IllegalArgumentException {
    public PatientAlreadyExistsException ( String message ) {
        super ( message );
    }
}

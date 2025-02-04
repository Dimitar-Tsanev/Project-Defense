package medical_clinics.shared.exception;

public class ExistingClinicException extends IllegalArgumentException {
    public ExistingClinicException ( String message ) {
        super ( message );
    }
}

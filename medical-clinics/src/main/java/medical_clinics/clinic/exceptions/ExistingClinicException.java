package medical_clinics.clinic.exceptions;

public class ExistingClinicException extends IllegalArgumentException {
    public ExistingClinicException ( String message ) {
        super ( message );
    }
}

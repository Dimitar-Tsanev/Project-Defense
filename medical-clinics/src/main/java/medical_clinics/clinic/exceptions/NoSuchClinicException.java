package medical_clinics.clinic.exceptions;

import java.util.NoSuchElementException;

public class NoSuchClinicException extends NoSuchElementException {
    public NoSuchClinicException ( String message ) {
        super ( message );
    }
}

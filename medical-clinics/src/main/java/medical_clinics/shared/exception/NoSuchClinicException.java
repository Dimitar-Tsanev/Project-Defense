package medical_clinics.shared.exception;

import java.util.NoSuchElementException;

public class NoSuchClinicException extends NoSuchElementException {
    public NoSuchClinicException ( String message ) {
        super ( message );
    }
}

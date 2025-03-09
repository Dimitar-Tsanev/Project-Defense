package medical_clinics.shared.exception;

import java.util.NoSuchElementException;

public class SpecialityException extends NoSuchElementException {
    public SpecialityException ( String message ) {
        super ( message );
    }
}
package medical_clinics.specialty.exceptions;

import java.util.NoSuchElementException;

public class SpecialityException extends NoSuchElementException {
    public SpecialityException ( String message ) {
        super ( message );
    }
}
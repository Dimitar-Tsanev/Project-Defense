package medical_clinics.patient.exceptions;

import java.util.NoSuchElementException;

public class PatientNotFoundException extends NoSuchElementException {
    public PatientNotFoundException ( String message ) {
        super ( message );
    }
}

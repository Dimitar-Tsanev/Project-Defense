package medical_clinics.physician.exceptions;

import java.util.NoSuchElementException;

public class PhysicianNotFoundException extends NoSuchElementException {
    public PhysicianNotFoundException ( String message ) {
        super ( message );
    }
}

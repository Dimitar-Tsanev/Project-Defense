package medical_clinics.shared.exception;

import java.util.NoSuchElementException;

public class PhysicianNotFoundException extends NoSuchElementException {
    public PhysicianNotFoundException ( String message ) {
        super ( message );
    }
}

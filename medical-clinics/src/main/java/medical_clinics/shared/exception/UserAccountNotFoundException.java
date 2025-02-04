package medical_clinics.shared.exception;

import java.util.NoSuchElementException;

public class UserAccountNotFoundException extends NoSuchElementException {
    public UserAccountNotFoundException ( String message ) {
        super ( message );
    }
}

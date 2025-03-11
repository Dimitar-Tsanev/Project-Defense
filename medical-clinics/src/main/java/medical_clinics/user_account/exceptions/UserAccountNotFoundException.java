package medical_clinics.user_account.exceptions;

import java.util.NoSuchElementException;

public class UserAccountNotFoundException extends NoSuchElementException {
    public UserAccountNotFoundException ( String message ) {
        super ( message );
    }
}

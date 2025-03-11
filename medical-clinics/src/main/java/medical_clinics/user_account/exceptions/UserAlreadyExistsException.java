package medical_clinics.user_account.exceptions;

public class UserAlreadyExistsException extends IllegalArgumentException {
    public UserAlreadyExistsException ( String message ) {
        super ( message );
    }
}

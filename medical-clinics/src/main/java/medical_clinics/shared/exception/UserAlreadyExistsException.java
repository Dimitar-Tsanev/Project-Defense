package medical_clinics.shared.exception;

public class UserAlreadyExistsException extends IllegalArgumentException {
    public UserAlreadyExistsException ( String message ) {
        super ( message );
    }
}

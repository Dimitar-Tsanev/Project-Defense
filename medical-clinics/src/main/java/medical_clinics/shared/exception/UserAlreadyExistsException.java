package medical_clinics.shared.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException ( String message ) {
        super ( message );
    }
}

package medical_clinics.shared.exception;

public class PhysicianAlreadyExistException extends IllegalArgumentException {
    public PhysicianAlreadyExistException ( String message ) {
        super ( message );
    }
}

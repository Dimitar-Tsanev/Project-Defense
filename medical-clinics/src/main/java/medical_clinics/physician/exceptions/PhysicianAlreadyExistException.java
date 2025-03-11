package medical_clinics.physician.exceptions;

public class PhysicianAlreadyExistException extends IllegalArgumentException {
    public PhysicianAlreadyExistException ( String message ) {
        super ( message );
    }
}

package medical_clinics.shared.exception;

public class PersonalInformationDontMatchException extends IllegalArgumentException {
    public PersonalInformationDontMatchException ( String message ) {
        super ( message );
    }
}

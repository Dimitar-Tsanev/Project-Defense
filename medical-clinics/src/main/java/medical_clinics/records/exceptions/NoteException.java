package medical_clinics.records.exceptions;

import lombok.Getter;

@Getter
public class NoteException extends RuntimeException {
    private int statusCode;

    public NoteException ( String message ) {
        super ( message );
    }

    public NoteException ( int statusCode, String message ) {
        super ( message );
        this.statusCode = statusCode;
    }
}

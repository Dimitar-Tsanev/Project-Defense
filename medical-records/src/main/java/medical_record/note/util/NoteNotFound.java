package medical_record.note.util;

public class NoteNotFound extends RuntimeException {
    public NoteNotFound ( String message ) {
        super ( message );
    }
}

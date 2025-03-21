package medical_record.note.service;

import medical_record.note.model.Note;
import medical_record.note.repository.NoteRepository;
import medical_record.note.web.TestNotesDtoBuilder;
import medical_record.note.web.dtos.NoteDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class NoteServiceITests {
    private static final UUID PHYSICIAN_ID = UUID.randomUUID ( );
    private static final UUID PATIENT_ID = UUID.randomUUID ( );

    @Autowired
    private NoteService noteService;

    @Autowired
    private NoteRepository noteRepository;

    @Test
    void when_addNote_then_NoteIsFound () {
        UUID noteId = noteService.addNote ( TestNotesDtoBuilder.buildValidImport (PATIENT_ID, PHYSICIAN_ID  ) );

        NoteDto noteDto = noteService.getNoteById ( noteId );
        List<NoteDto> patientRecord = noteService.getPatientMedicalRecord ( PATIENT_ID );
        List<NoteDto> physicianNotes = noteService.getPhysicianNotes ( PHYSICIAN_ID );

        List<Note> notes = noteRepository.findAll ( );

        assertEquals ( 1, patientRecord.size ( ) );
        assertEquals ( 1, physicianNotes.size ( ) );
        assertEquals ( 1, notes.size( ) );

        assertEquals ( noteId, notes.getFirst ().getId ( ) );
        assertEquals ( noteId, noteDto.getNoteId () );

        assertEquals ( PATIENT_ID, patientRecord.getFirst ().getPatientId ());
        assertEquals ( PHYSICIAN_ID, physicianNotes.getFirst ().getPhysicianId ());
    }

}

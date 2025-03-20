package medical_record.note.service;
import medical_record.note.model.Note;
import medical_record.note.repository.NoteRepository;
import medical_record.note.util.NoteNotFound;
import medical_record.note.web.dtos.NoteDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NoteServiceUTest {

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteService noteService;

    @Test
    void when_addNote_thenNoteIsSaved() {
        UUID noteId = UUID.randomUUID();

        NoteDto noteDto = new NoteDto();

        when(noteRepository.save ( any () )).thenReturn (Note.builder ().id ( noteId ).build () );

        UUID savedNoteId = noteService.addNote ( noteDto );

        verify ( noteRepository, times( 1 ) ).save ( any () );

        assertEquals(noteId, savedNoteId);
    }

    @Test
    void when_getPatientMedicalRecord_WithPatientIdNotFound_ReturnEmptyList(){
        UUID id = UUID.randomUUID();

        when(noteRepository.findAllByPatientId ( id )).thenReturn (new ArrayList<> ());

        List<NoteDto> noteDtos = noteService.getPatientMedicalRecord ( id );

        assertTrue( noteDtos.isEmpty () );
        verify ( noteRepository, times( 1 ) ).findAllByPatientId ( id );
    }

    @Test
    void when_getPatientMedicalRecord_WithPatientIdFound_ReturnMedicalRecord(){
        UUID id = UUID.randomUUID();
        List<Note> notes = List.of (new Note (), new Note (), new Note ());

        when ( noteRepository.findAllByPatientId ( id ) ).thenReturn (notes);

        List<NoteDto> noteDtos = noteService.getPatientMedicalRecord ( id );

        assertEquals (3, noteDtos.size () );
        verify ( noteRepository, times( 1 ) ).findAllByPatientId ( id );
    }
    @Test
    void when_getPhysicianNotes_WithPatientIdNotFound_ReturnEmptyList(){
        UUID id = UUID.randomUUID();

        when(noteRepository.findAllByPhysicianId ( id )).thenReturn (new ArrayList<> ());

        List<NoteDto> noteDtos = noteService.getPhysicianNotes ( id );

        assertTrue( noteDtos.isEmpty () );
        verify ( noteRepository, times( 1 ) ).findAllByPhysicianId (  id );
    }

    @Test
    void when_getPhysicianNotes_WithPatientIdFound_ReturnListOfPhysicianNotes(){
        UUID id = UUID.randomUUID();
        List<Note> notes = List.of (new Note (), new Note (), new Note ());

        when ( noteRepository.findAllByPhysicianId( id ) ).thenReturn (notes);

        List<NoteDto> noteDtos = noteService.getPhysicianNotes ( id );

        assertEquals (3, noteDtos.size () );
        verify ( noteRepository, times( 1 ) ).findAllByPhysicianId ( id );
    }

    @Test
    void when_getNoteById_WithIdNotFound_ThrowsException(){
        UUID id = UUID.randomUUID();

        when ( noteRepository.findById ( id ) ).thenReturn ( Optional.empty () );

        assertThrows ( NoteNotFound.class, () -> noteService.getNoteById ( id ) );
        verify ( noteRepository, times( 1 ) ).findById ( id );
    }

    @Test
    void when_getNoteById_WithIdFound_ReturnNote(){
        UUID id = UUID.randomUUID();

        when ( noteRepository.findById ( id ) ).thenReturn ( Optional.of (Note.builder ().id ( id ).build ( ) ) );

        NoteDto noteDto = noteService.getNoteById ( id );

        assertNotNull ( noteDto );
        assertEquals ( id, noteDto.getNoteId () );

        verify ( noteRepository, times( 1 ) ).findById ( id );
    }
}

package medical_record.note.service;

import lombok.AllArgsConstructor;
import medical_record.note.model.Note;
import medical_record.note.repository.NoteRepository;
import medical_record.note.util.NoteMapper;
import medical_record.note.util.NoteNotFound;
import medical_record.note.web.dtos.NoteDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor

@Service
public class NoteService {
    private final NoteRepository noteRepository;

    public UUID addNote ( NoteDto noteDto ) {
        return noteRepository.save ( NoteMapper.mapFromDto ( noteDto ) ).getId ( );
    }

    public List<NoteDto> getPatientMedicalRecord ( UUID patientId ) {
        return mapToDto ( noteRepository.findAllByPatientId ( patientId ) );
    }

    public List<NoteDto> getPhysicianNotes ( UUID physicianID ) {
        return mapToDto ( noteRepository.findAllByPhysicianId ( physicianID ) );
    }

    public NoteDto getNoteById ( UUID noteId ) {
        Optional<Note> note = noteRepository.findById ( noteId );

        if ( note.isPresent ( ) ) {
            return NoteMapper.mapToDto ( note.get ( ) );
        }

        throw new NoteNotFound ( "Note not found" );
    }

    private List<NoteDto> mapToDto ( List<Note> notes ) {
        if ( notes.isEmpty ( ) ) {
            return new ArrayList<> ( );
        }

        return notes.stream ( ).map ( NoteMapper::mapToDto ).toList ( );
    }
}

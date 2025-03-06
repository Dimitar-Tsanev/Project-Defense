package medical_record.note.service;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import medical_record.note.model.Note;
import medical_record.note.repository.NoteRepository;
import medical_record.note.util.NoteMapper;
import medical_record.note.web.dtos.NoteDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor

@Service
public class NoteService {
    private final NoteRepository noteRepository;

    public NoteDto addNote ( @Valid NoteDto noteDto ) {
       Note note =  noteRepository.save ( NoteMapper.mapFromDto ( noteDto ) );

       return NoteMapper.mapToDto ( note );
    }

    public List<NoteDto> getPatientMedicalRecord ( UUID patientId ) {
        return noteRepository.findAllByPatientId(patientId).stream().map ( NoteMapper::mapToDto ).toList ( );
    }

    public List<NoteDto> getPhysicianNotes ( UUID physicianID ) {
        return noteRepository.findAllByPhysicianId(physicianID).stream ().map ( NoteMapper::mapToDto ).toList ();
    }
}

package medical_record.note.web;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import medical_record.note.service.NoteService;
import medical_record.note.web.dtos.NoteDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@RestController("api/v1/notes")
public class NotesController {
    private final NoteService noteService;

    @PostMapping
    public ResponseEntity<NoteDto> createNote( @Valid NoteDto note) {
        NoteDto createdNote = noteService.addNote(note);
        return ResponseEntity.status ( HttpStatus.CREATED ).body(createdNote);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<Collection<NoteDto>> getNoteById( @PathVariable UUID patientId) {
        List<NoteDto> patientMedicalRecord = noteService.getPatientMedicalRecord (patientId);
        return ResponseEntity.status(HttpStatus.OK).body(patientMedicalRecord);
    }

    @GetMapping("physician/{physicianID}")
    public ResponseEntity<Collection<NoteDto>> getPhysicianNotes( @PathVariable UUID physicianID) {
        List<NoteDto> patientMedicalRecord = noteService.getPhysicianNotes(physicianID);
        return ResponseEntity.status(HttpStatus.OK).body(patientMedicalRecord);
    }
}

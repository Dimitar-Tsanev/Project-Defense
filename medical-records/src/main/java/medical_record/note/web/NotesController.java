package medical_record.note.web;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import medical_record.note.service.NoteService;
import medical_record.note.web.dtos.NoteDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@RestController("/api/v1/notes")
public class NotesController {
    private final NoteService noteService;

    @PostMapping
    public ResponseEntity<Void> createNote ( @Valid @RequestBody NoteDto note ) {
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest ( )
                .path ( "/{id}" )
                .buildAndExpand (
                        noteService.addNote ( note )
                )
                .toUri ( );

        return ResponseEntity.created ( location ).build ( );
    }

    @GetMapping("/{noteId}")
    public ResponseEntity<NoteDto> getNote ( @PathVariable UUID noteId ) {
        return ResponseEntity.ok ( noteService.getNoteById ( noteId ) );
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<Collection<NoteDto>> getNoteById ( @PathVariable UUID patientId ) {
        List<NoteDto> patientMedicalRecord = noteService.getPatientMedicalRecord ( patientId );
        return ResponseEntity.status ( HttpStatus.OK ).body ( patientMedicalRecord );
    }

    @GetMapping("/physician/{physicianId}")
    public ResponseEntity<Collection<NoteDto>> getPhysicianNotes ( @PathVariable UUID physicianId ) {
        List<NoteDto> patientMedicalRecord = noteService.getPhysicianNotes ( physicianId );
        return ResponseEntity.status ( HttpStatus.OK ).body ( patientMedicalRecord );
    }
}

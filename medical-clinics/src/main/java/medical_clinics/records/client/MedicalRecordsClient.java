package medical_clinics.records.client;

import medical_clinics.records.client.dto.NoteDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collection;
import java.util.UUID;

@FeignClient(name = "medical-records-notes", url = "http://localhost:8081/api/v1/notes")
public interface MedicalRecordsClient {

    @PostMapping
    ResponseEntity<String> createNote ( @RequestBody NoteDto noteDto );

    @GetMapping("/{noteId}")
    ResponseEntity<NoteDto> getNote ( @PathVariable(value = "noteId") UUID noteId );

    @GetMapping("/patient/{patientId}")
    ResponseEntity<Collection<NoteDto>> getPatientRecord ( @PathVariable(value = "patientId") UUID patientId );

    @GetMapping("/physician/{physicianID}")
    ResponseEntity<Collection<NoteDto>> getPhysicianNotes ( @PathVariable(value = "physicianID") UUID physicianID );
}

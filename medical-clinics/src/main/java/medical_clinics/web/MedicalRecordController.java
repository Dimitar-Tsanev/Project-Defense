package medical_clinics.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import medical_clinics.records.service.RecordsService;
import medical_clinics.web.dto.NewNoteRequest;
import medical_clinics.web.dto.response.NoteResponse;
import medical_clinics.web.exception_handler.ExceptionResponse;
import medical_clinics.web.validation.new_note_patient.NewNoteConstrainPatientNecessaryInformation;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor

@RestController
@RequestMapping("/medical-records")
public class MedicalRecordController {

    private final RecordsService recordsService;

    @Operation(
            summary = "Get a not by it's id",
            security = @SecurityRequirement(name = "Bearer token")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = NoteResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Bearer token not found or invalid",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Note not found",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    @GetMapping("/note/{noteId}")
    public ResponseEntity<NoteResponse> getNote ( @PathVariable UUID noteId ) {
        return ResponseEntity.ok ( recordsService.getNoteById ( noteId ) );
    }

    @Operation(
            summary = "Get a patient medical record by patient id",
            security = @SecurityRequirement(name = "Bearer token")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = NoteResponse[].class))
            ),
            @ApiResponse(responseCode = "401", description = "Bearer token not found or invalid",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Patient not found",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<NoteResponse>> getPatientRecord ( @PathVariable UUID patientId ) {
        return ResponseEntity.ok ( recordsService.getPatientRecord ( patientId ) );
    }

    @Operation(
            summary = "Get all physician notes by physician id",
            security = @SecurityRequirement(name = "Bearer token", scopes = "ROLE_PHYSICIAN")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = NoteResponse[].class))
            ),
            @ApiResponse(responseCode = "401", description = "Bearer token not found or invalid",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Physician not found",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    @PreAuthorize("hasAnyRole('PHYSICIAN','ADMIN')")
    @GetMapping("/physician/{accountId}")
    public ResponseEntity<List<NoteResponse>> getPhysicianNotes ( @PathVariable UUID accountId ) {
        return ResponseEntity.ok ( recordsService.getPhysicianNotes ( accountId ) );
    }

    @Operation(
            summary = "Create new medical record note",
            security = @SecurityRequirement(name = "Bearer token", scopes = "ROLE_PHYSICIAN")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Note created successfully",
                    headers = @Header(name = HttpHeaders.LOCATION, description = "link to the note")
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input provided",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Bearer token not found or invalid",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Physician or patient not found",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    @PreAuthorize("hasAnyRole('PHYSICIAN','ADMIN')")
    @PostMapping("/note/new/physician/{accountId}")
    public ResponseEntity<Void> addNewNote (
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Information for note creation", required = true,
                    content = @Content(schema = @Schema(implementation = NewNoteRequest.class))
            )
            @PathVariable UUID accountId, @RequestBody @Valid NewNoteRequest note,
            @RequestParam @Valid @NewNoteConstrainPatientNecessaryInformation UUID patientId ) {


        URI location = ServletUriComponentsBuilder
                .fromPath ( "http://localhost:8080/api/v0/medical-records/note/" )
                .path ( "{id}" )
                .buildAndExpand (
                        recordsService.createNote ( accountId, patientId, note )
                )
                .toUri ( );

        return ResponseEntity.created ( location ).build ( );
    }
}

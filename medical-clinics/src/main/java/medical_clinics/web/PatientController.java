package medical_clinics.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import medical_clinics.patient.service.PatientService;
import medical_clinics.web.dto.CreatePatient;
import medical_clinics.web.dto.response.PatientInfo;
import medical_clinics.web.exception_handler.ExceptionResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor

@RestController
@RequestMapping("/patients")
public class PatientController {

    private PatientService patientService;

    @Operation(
            summary = "Create new patient",
            security = @SecurityRequirement(name = "Bearer token", scopes = "ROLE_PHYSICIAN")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Patient created successfully",
                    headers = @Header(name = HttpHeaders.LOCATION, description = "Link to created physician")
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
            @ApiResponse(responseCode = "409", description = "Conflict with another user information",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    @PostMapping("/")
    @PreAuthorize("hasRole('PHYSICIAN')")
    public ResponseEntity<Void> addPatient ( @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Information for physician creation", required = true,
            content = @Content(schema = @Schema(implementation = CreatePatient.class)
            )) @RequestBody @Valid CreatePatient patient ) {

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest ( )
                .path ( "/{id}" )
                .buildAndExpand (
                        patientService.addPatient ( patient )
                )
                .toUri ( );

        return ResponseEntity.created ( location ).build ( );
    }

    @Operation(summary = "Get specific patient information",
            security = @SecurityRequirement(name = "Bearer token", scopes = "ROLE_PHYSICIAN")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = PatientInfo.class))
            ),
            @ApiResponse(responseCode = "401", description = "Bearer token not found or invalid",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Patient data not found",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    @GetMapping("/{patientId}")
    @PreAuthorize("hasRole('PHYSICIAN')")
    public ResponseEntity<PatientInfo> getPatient ( @PathVariable UUID patientId ) {
        return ResponseEntity.ok ( patientService.getPatientInfoById ( patientId ) );
    }

    @Operation(
            summary = """
                            Filters patient information by optional parameters:
                            phone, email and country and identification code of the patient.
                            Returns list with one patient if presented parameters find same patient.
                            Returns list of patients if the phone number a email or
                            country and identification belong to different patients.
                            Returns empty list if no patient is found for each presented parameter;
                    """,

            security = @SecurityRequirement(name = "Bearer token", scopes = "ROLE_PHYSICIAN")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = PatientInfo[].class))
            ),
            @ApiResponse(responseCode = "401", description = "Bearer token not found or invalid",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    @GetMapping("/filter/")
    @PreAuthorize("hasRole('PHYSICIAN')")
    public ResponseEntity<List<PatientInfo>> findPatient (
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Map<String, String> countryAndIdentificationCode ) {

        return ResponseEntity.ok (
                patientService.findPatient ( phoneNumber, email, countryAndIdentificationCode )
        );
    }

    @Operation(
            summary = "Set patient country and identification code required for creation of new note",
            security = @SecurityRequirement(name = "Bearer token", scopes = "ROLE_PHYSICIAN")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Patient updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input provided",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Bearer token not found or invalid",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Patient data not found",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "409", description = "Conflict with another user information",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    @PutMapping("/{patientId}")
    @PreAuthorize("hasRole('PHYSICIAN')")
    public ResponseEntity<Void> setPatientCountryAndIdentificationCode (
            @PathVariable UUID patientId,
            @RequestParam @Valid @Pattern(
                    regexp = "^[A-Za-z ]+$", message = "{country.unsupported.characters}"
            ) String country,
            @RequestParam @Valid @Pattern(
                    regexp = "^[0-9A-Z]+([.-]*[0-9A-Z]+)+$", message = "{identification.code.unsupported.character}"
            ) String identificationCode ) {

        patientService.updatePatientInfo ( patientId, country, identificationCode );
        return ResponseEntity.noContent ( ).build ( );
    }
}

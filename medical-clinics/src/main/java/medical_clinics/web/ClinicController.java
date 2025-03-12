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
import medical_clinics.clinic.services.ClinicService;
import medical_clinics.web.dto.CreateEditClinicRequest;
import medical_clinics.web.dto.response.ClinicDetails;
import medical_clinics.web.dto.response.ClinicShortInfo;
import medical_clinics.web.exception_handler.ExceptionResponse;
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
@RequestMapping("/clinics")
public class ClinicController {

    private final ClinicService clinicService;

    @Operation(summary = "Get all clinics short information")
    @ApiResponses(
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = ClinicShortInfo[].class))
            )
    )
    @GetMapping("/")
    public ResponseEntity<List<ClinicShortInfo>> getAllClinics () {
        return ResponseEntity.ok ( clinicService.getAllClinics ( ) );
    }

    @Operation(
            summary = "Create new clinic",
            security = @SecurityRequirement(name = "Bearer token", scopes = "ROLE_ADMIN")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Clinic created successfully",
                    headers = @Header(name = HttpHeaders.LOCATION, description = "link to clinic details")
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
            @ApiResponse(responseCode = "409", description = "Conflict clinic in same city and address found",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    @PostMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> addNewClinic ( @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Information for clinic creation ", required = true,
            content = @Content(schema = @Schema(implementation = CreateEditClinicRequest.class)
            )) @RequestBody @Valid CreateEditClinicRequest newClinic ) {

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest ( )
                .path ( "/{id}" )
                .buildAndExpand (
                        clinicService.addClinic ( newClinic )
                )
                .toUri ( );

        return ResponseEntity.created ( location ).build ( );
    }

    @Operation(summary = "Get specific clinic detailed information")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = ClinicDetails.class))
            ),
            @ApiResponse(responseCode = "404", description = "Clinic data not found",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    @GetMapping("/{clinicId}")
    public ResponseEntity<ClinicDetails> getClinicInfo ( @PathVariable UUID clinicId ) {
        return ResponseEntity.ok ( clinicService.getClinicById ( clinicId ) );
    }

    @Operation(
            summary = "Update clinic information",
            security = @SecurityRequirement(name = "Bearer token", scopes = "ROLE_ADMIN")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Clinic edited successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input provided",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Bearer token not found or invalid",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Clinic data not found",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "409", description = "Conflict existing clinic on new city and address found",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    @PutMapping("/{clinicId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> editClinic ( @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Information for clinic update ", required = true,
            content = @Content(schema = @Schema(implementation = CreateEditClinicRequest.class)
            )) @PathVariable UUID clinicId, @RequestBody @Valid CreateEditClinicRequest editClinic ) {

        clinicService.updateClinic ( clinicId, editClinic );
        return ResponseEntity.noContent ( ).build ( );
    }

    @Operation(
            summary = "Delete clinic information",
            security = @SecurityRequirement(name = "Bearer token", scopes = "ROLE_ADMIN")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Clinic deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Bearer token not found or invalid",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Clinic data not found",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
    })
    @DeleteMapping("/{clinicId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteClinic ( @PathVariable UUID clinicId ) {
        clinicService.deleteClinic ( clinicId );
        return ResponseEntity.noContent ( ).build ( );
    }
}

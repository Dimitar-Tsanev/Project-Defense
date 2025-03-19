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
import medical_clinics.physician.service.PhysicianService;
import medical_clinics.web.dto.CreatePhysician;
import medical_clinics.web.dto.PhysicianEditRequest;
import medical_clinics.web.dto.response.PhysicianInfo;
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
@RequestMapping("/physicians")
public class PhysicianController {
    private final PhysicianService physicianService;

    @Operation(
            summary = "Create new physician",
            security = @SecurityRequirement(name = "Bearer token", scopes = "ROLE_ADMIN")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Physician created successfully",
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
            @ApiResponse(responseCode = "404", description = "Clinic (workplace) not found",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "409", description = "Conflict with another user information",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    @PostMapping("/physician/new")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> addPhysician ( @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Information for physician creation", required = true,
            content = @Content(schema = @Schema(implementation = CreatePhysician.class)
            )) @RequestBody @Valid CreatePhysician physician ) {

        URI location = ServletUriComponentsBuilder
                .fromPath ( "http://localhost:8080/api/v0/physicians/physician/" )
                .path ( "{id}" )
                .buildAndExpand (
                        physicianService.addPhysician ( physician )
                )
                .toUri ( );

        return ResponseEntity.created ( location ).build ( );
    }

    @Operation(
            summary = "Update physician information",
            security = @SecurityRequirement(name = "Bearer token", scopes = "ROLE_ADMIN")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Physician updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input provided",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Bearer token not found or invalid",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Physician data not found",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "409", description = "Information conflict with another user",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    @PutMapping("/physician/{physicianId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updatePhysician ( @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Information for physician update", required = true,
            content = @Content(schema = @Schema(implementation = PhysicianEditRequest.class)
            )) @PathVariable UUID physicianId, @RequestBody @Valid PhysicianEditRequest physician ) {

        physicianService.editPhysician ( physicianId, physician );
        return ResponseEntity.noContent ( ).build ( );
    }

    @Operation(
            summary = "Dismiss physician set workplace to null",
            security = @SecurityRequirement(name = "Bearer token", scopes = "ROLE_ADMIN")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Dismiss physician successfully"),
            @ApiResponse(responseCode = "401", description = "Bearer token not found or invalid",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Physician not found",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
    })
    @DeleteMapping("/physician/{physicianId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> dismissPhysician ( @PathVariable UUID physicianId ) {

        physicianService.dismissPhysician ( physicianId );
        return ResponseEntity.noContent ( ).build ( );
    }

    @Operation(summary = "Get list of physicians by there workplace ( clinicId ) and speciality (specialityId)")
    @ApiResponses(
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = PhysicianInfo[].class))
            )
    )
    @GetMapping("/{clinicId}/{specialityId}")
    public ResponseEntity<List<PhysicianInfo>> getPhysiciansByClinicAndSpeciality (
            @PathVariable UUID clinicId, @PathVariable UUID specialityId ) {

        return ResponseEntity.ok ( physicianService.getPhysiciansByClinicAndSpeciality ( clinicId, specialityId ) );
    }

    @Operation(summary = "Get specific physician information")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = PhysicianInfo.class))
            ),
            @ApiResponse(responseCode = "404", description = "Physician data not found",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    @GetMapping("/physician/{physicianId}")
    public ResponseEntity<PhysicianInfo> getPhysicianInfo ( @PathVariable UUID physicianId ) {
        return ResponseEntity.ok ( physicianService.getPhysicianInfo ( physicianId ) );
    }
}

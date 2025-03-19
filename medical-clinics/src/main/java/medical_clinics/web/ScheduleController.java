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
import medical_clinics.schedule.services.DailyScheduleService;
import medical_clinics.schedule.services.TimeSlotService;
import medical_clinics.web.dto.NewDaySchedule;
import medical_clinics.web.dto.response.PatientAppointment;
import medical_clinics.web.dto.response.schedule_private.PhysicianDaySchedulePrivate;
import medical_clinics.web.dto.response.schedule_public.PhysicianDaySchedulePublic;
import medical_clinics.web.exception_handler.ExceptionResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor

@RestController
@RequestMapping("/schedules")
public class ScheduleController {
    private final DailyScheduleService dailyScheduleService;
    private final TimeSlotService timeSlotService;
    private final PhysicianService physicianService;

    @Operation(
            summary = "Generate physician schedule",
            security = @SecurityRequirement(name = "Bearer token", scopes = {"ROLE_ADMIN", "ROLE_PHYSICIAN"})
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Physician schedule created successfully",
                    headers = @Header(name = HttpHeaders.LOCATION, description = "Link to created schedule")
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
            @ApiResponse(responseCode = "404", description = "Physician not found. ",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "409", description = "Conflict with clinic workdays or time",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    @PostMapping("/new/physician/{accountId}")
    @PreAuthorize("hasAnyRole('ADMIN','PHYSICIAN')")
    public ResponseEntity<Void> generateSchedule (
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Information for physician schedule creation", required = true,
                    content = @Content(schema = @Schema(implementation = NewDaySchedule[].class))
            )
            @RequestBody List<@Valid NewDaySchedule> newDaySchedule,
            @PathVariable UUID accountId ) {

        UUID id = physicianService.generateSchedule ( accountId, newDaySchedule );

        String path = "http://localhost:8080/api/v1/schedules/physician/" + id.toString ( );

        return ResponseEntity.status ( HttpStatus.CREATED ).header ( HttpHeaders.LOCATION, path ).build ( );
    }

    @Operation(
            summary = "Get list of physician schedules with appointed patients",
            security = @SecurityRequirement(name = "Bearer token", scopes = {"ROLE_ADMIN", "ROLE_PHYSICIAN"})
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = PhysicianDaySchedulePrivate[].class))
            ),
            @ApiResponse(responseCode = "401", description = "Bearer token not found or invalid",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    @GetMapping("/physician/{accountId}")
    @PreAuthorize("hasAnyRole('ADMIN','PHYSICIAN')")
    public ResponseEntity<List<PhysicianDaySchedulePrivate>> getPhysicianSchedules ( @PathVariable UUID accountId ) {
        return ResponseEntity.ok ( dailyScheduleService.getPrivatePhysicianSchedules ( accountId ) );
    }

    @Operation(
            summary = "Get list of physician schedules",
            security = @SecurityRequirement(name = "Bearer token")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = PhysicianDaySchedulePublic[].class))
            ),
            @ApiResponse(responseCode = "401", description = "Bearer token not found or invalid",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    @GetMapping("/")
    public ResponseEntity<List<PhysicianDaySchedulePublic>> getPublicPhysicianSchedules ( @RequestParam UUID physicianId ) {
        return ResponseEntity.ok ( dailyScheduleService.getPublicPhysicianSchedules ( physicianId ) );
    }

    @Operation(
            summary = "Reserve timeslot (make appointment)",
            security = @SecurityRequirement(name = "Bearer token")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Reservation successfully"),
            @ApiResponse(responseCode = "401", description = "Bearer token not found or invalid",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Timeslot not found. ",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "409", description = "Data conflict timeslot not available",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    @PatchMapping("/appointments/{timeslotId}")
    public ResponseEntity<Void> makeAppointment ( @RequestParam UUID accountId, @PathVariable UUID timeslotId ) {
        timeSlotService.makeAppointment ( accountId, timeslotId );
        return ResponseEntity.noContent ( ).build ( );
    }

    @Operation(
            summary = "Release timeslot (remove appointment)",
            security = @SecurityRequirement(name = "Bearer token")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Release successfully"),
            @ApiResponse(responseCode = "401", description = "Bearer token not found or invalid",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Timeslot not found.",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "409", description = "Data conflict timeslot not belong to these account",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    @DeleteMapping("/appointments/{timeslotId}")
    public ResponseEntity<Void> releaseAppointment ( @RequestParam UUID accountId, @PathVariable UUID timeslotId ) {
        timeSlotService.releaseAppointment ( accountId, timeslotId );
        return ResponseEntity.noContent ( ).build ( );
    }

    @Operation(
            summary = "Get list of patient appointments",
            security = @SecurityRequirement(name = "Bearer token")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = PatientAppointment[].class))
            ),
            @ApiResponse(responseCode = "401", description = "Bearer token not found or invalid",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<PatientAppointment>> getPatientAppointments ( @PathVariable UUID patientId ) {
        return ResponseEntity.ok ( timeSlotService.getPatientAppointments ( patientId ) );
    }

    @Operation(
            summary = "Block day schedule",
            security = @SecurityRequirement(name = "Bearer token", scopes = {"ROLE_ADMIN", "ROLE_PHYSICIAN"})
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Physician schedule blocked successfully"),
            @ApiResponse(responseCode = "401", description = "Bearer token not found or invalid",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Schedule not found. ",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "409", description = "Conflict reserved timeslot cannot be blocked",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    @PatchMapping("/physician/{accountId}")
    @PreAuthorize("hasAnyRole('ADMIN','PHYSICIAN')")
    public ResponseEntity<Void> inactivateSchedule ( @PathVariable UUID accountId, @RequestParam LocalDate localDate ) {
        dailyScheduleService.inactivateDaySchedule ( accountId, localDate );
        return ResponseEntity.noContent ( ).build ( );
    }

    @Operation(
            summary = "Block timeslot",
            security = @SecurityRequirement(name = "Bearer token", scopes = {"ROLE_ADMIN", "ROLE_PHYSICIAN"})
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Timeslot blocked successfully"),
            @ApiResponse(responseCode = "401", description = "Bearer token not found or invalid",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Timeslot not found. ",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "409", description = "Conflict reserved timeslot cannot be blocked",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    @PatchMapping("/timeslot/{timeslotId}")
    @PreAuthorize("hasAnyRole('ADMIN','PHYSICIAN')")
    public ResponseEntity<Void> inactivateTimeslot ( @PathVariable UUID timeslotId ) {
        timeSlotService.inactivate ( timeslotId );
        return ResponseEntity.noContent ( ).build ( );
    }
}

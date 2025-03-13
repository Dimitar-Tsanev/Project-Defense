package medical_clinics.web;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import medical_clinics.physician.service.PhysicianService;
import medical_clinics.schedule.services.DailyScheduleService;
import medical_clinics.schedule.services.TimeSlotService;
import medical_clinics.web.dto.NewDaySchedule;
import medical_clinics.web.dto.response.PatientAppointment;
import medical_clinics.web.dto.response.PhysicianDaySchedule;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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

    @PostMapping("/new/physician/{physicianId}")
    @PreAuthorize("hasAnyRole('ADMIN','PHYSICIAN')")
    public ResponseEntity<Void> generateSchedule (
            @PathVariable UUID physicianId,
            @RequestBody List<@Valid NewDaySchedule> newDaySchedule ) {

        physicianService.generateSchedule ( physicianId, newDaySchedule );

        URI location = ServletUriComponentsBuilder
                .fromPath ( "http://localhost:8080/api/v1/schedules/physician/{physicianId}" )
                .path ( "/{id}" )
                .buildAndExpand (
                        physicianId
                )
                .toUri ( );

        return ResponseEntity.created ( location ).build ( );
    }

    @GetMapping("/physician/{physicianId}")
    @PreAuthorize("hasAnyRole('ADMIN','PHYSICIAN')")
    public ResponseEntity<List<PhysicianDaySchedule>> getPhysicianSchedules ( @PathVariable UUID physicianId ) {
        return ResponseEntity.ok ( dailyScheduleService.getPrivatePhysicianSchedules ( physicianId ) );
    }

    @GetMapping("/")
    public ResponseEntity<List<PhysicianDaySchedule>> getPublicPhysicianSchedules ( @RequestParam UUID physicianId ) {
        return ResponseEntity.ok ( dailyScheduleService.getPublicPhysicianSchedules ( physicianId ) );
    }

    @PatchMapping("/appointments/{appointmentId}")
    public ResponseEntity<Void> makeAppointment ( @RequestParam UUID accountId, @PathVariable UUID appointmentId ) {
        timeSlotService.makeAppointment ( accountId, appointmentId );
        return ResponseEntity.noContent ( ).build ( );
    }

    @DeleteMapping("/appointments/{appointmentId}")
    public ResponseEntity<Void> releaseAppointment ( @RequestParam UUID accountId, @PathVariable UUID appointmentId ) {
        timeSlotService.releaseAppointment ( accountId, appointmentId );
        return ResponseEntity.noContent ( ).build ( );
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<PatientAppointment>> getPatientAppointments ( @PathVariable UUID patientId ) {
        return ResponseEntity.ok ( timeSlotService.getPatientAppointments ( patientId ) );
    }

    @PatchMapping("/physician/{physicianId}")
    @PreAuthorize("hasAnyRole('ADMIN','PHYSICIAN')")
    public ResponseEntity<Void> inactivateSchedule ( @PathVariable UUID physicianId, @RequestParam LocalDate localDate ) {
        dailyScheduleService.inactivateDaySchedule ( physicianId, localDate );
        return ResponseEntity.noContent ( ).build ( );
    }

    @PatchMapping("/timeslot/{timeslotId}")
    @PreAuthorize("hasAnyRole('ADMIN','PHYSICIAN')")
    public ResponseEntity<Void> inactivateTimeslot ( @PathVariable UUID timeslotId ) {
        timeSlotService.inactivate ( timeslotId );
        return ResponseEntity.noContent ( ).build ( );
    }
}

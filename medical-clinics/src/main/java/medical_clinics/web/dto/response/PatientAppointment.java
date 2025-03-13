package medical_clinics.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder

public class PatientAppointment {
    private UUID id;

    private LocalDate appointmentDate;

    private LocalTime startTime;

    private String physician;

    private String address;
}

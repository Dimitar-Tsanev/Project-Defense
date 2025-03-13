package medical_clinics.web.dto.response.apointmet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import medical_clinics.schedule.models.Status;
import medical_clinics.web.dto.response.PatientInfo;

import java.time.LocalTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter

public class DayAppointmentPrivate implements DayAppointment {
    private UUID id;

    private Status status;

    private LocalTime startTime;

    private PatientInfo patientInfo;
}

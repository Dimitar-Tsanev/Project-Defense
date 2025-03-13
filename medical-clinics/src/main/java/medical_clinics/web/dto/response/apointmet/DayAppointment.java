package medical_clinics.web.dto.response.apointmet;

import medical_clinics.schedule.models.Status;

import java.time.LocalTime;
import java.util.UUID;

public interface DayAppointment {
    UUID getId ();

    Status getStatus ();

    LocalTime getStartTime ();
}

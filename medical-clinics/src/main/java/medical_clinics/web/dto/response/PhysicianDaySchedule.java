package medical_clinics.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import medical_clinics.web.dto.response.apointmet.DayAppointment;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter

public class PhysicianDaySchedule {
    private UUID id;

    private LocalDate date;

    private List<DayAppointment> schedule;
}

package medical_clinics.schedule.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
public class ArchivedSchedules {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Basic(optional = false)
    private LocalDate date;

    @Basic(optional = false)
    private UUID physicianId;

    @Enumerated(EnumType.STRING)
    @Basic(optional = false)
    private Status status;

    private UUID patientId;

    @Basic(optional = false)
    private LocalTime startTime;

    @Basic(optional = false)
    private Integer durationInMinutes;
}

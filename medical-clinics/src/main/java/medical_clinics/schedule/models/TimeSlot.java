package medical_clinics.schedule.models;

import jakarta.persistence.*;
import lombok.*;
import medical_clinics.patient.model.Patient;

import java.time.LocalTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter

@Entity
public class TimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Basic(optional = false)
    private Status status;

    @Basic(optional = false)
    private LocalTime startTime;

    @Basic(optional = false)
    private Integer durationInMinutes;

    @ManyToOne
    @JoinColumn
    private Patient patient;

    @ManyToOne
    @JoinColumn(nullable = false)
    private DailySchedule dailySchedule;
}

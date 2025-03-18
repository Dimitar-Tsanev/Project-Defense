package medical_clinics.schedule.models;

import jakarta.persistence.*;
import lombok.*;
import medical_clinics.physician.model.Physician;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter

@Entity
public class DailySchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Basic(optional = false)
    private LocalDate date;

    @Basic(optional = false)
    private LocalTime startTime;

    @Basic(optional = false)
    private LocalTime endTime;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Physician physician;

    @OneToMany(mappedBy = "dailySchedule", fetch = FetchType.EAGER)
    @OrderBy("startTime ASC")
    private Collection<TimeSlot> timeSlots;

}

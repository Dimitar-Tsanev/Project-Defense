package medical_clinics.schedule.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import medical_clinics.physician.model.Physician;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data

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
    private Collection<TimeSlot> timeSlots;

}

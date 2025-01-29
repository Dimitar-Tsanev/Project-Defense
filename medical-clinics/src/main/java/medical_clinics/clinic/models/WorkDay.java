package medical_clinics.clinic.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data

@Entity
public class WorkDay {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DaysOfWeek daysOfWeek;

    @Basic(optional = false)
    private LocalTime startOfWorkingDay;

    @Basic(optional = false)
    private LocalTime endOfWorkingDay;
}

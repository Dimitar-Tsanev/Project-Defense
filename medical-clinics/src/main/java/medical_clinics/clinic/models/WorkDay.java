package medical_clinics.clinic.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter

@Entity
public class WorkDay {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DaysOfWeek dayOfWeek;

    @Basic(optional = false)
    private LocalTime startOfWorkingDay;

    @Basic(optional = false)
    private LocalTime endOfWorkingDay;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Clinic clinic;
}

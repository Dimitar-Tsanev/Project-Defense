package medical_clinics.physician.model;

import jakarta.persistence.*;
import lombok.*;
import medical_clinics.clinic.models.Clinic;
import medical_clinics.patient.model.Patient;
import medical_clinics.schedule.models.DailySchedule;
import medical_clinics.specialty.model.Specialty;
import medical_clinics.user_account.model.UserAccount;
import java.util.List;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data

@Entity
public class Physician {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Basic(optional = false)
    @EqualsAndHashCode.Exclude
    private String firstName;

    @Basic(optional = false)
    @EqualsAndHashCode.Exclude
    private String lastName;

    @Column(unique = true, nullable = false)
    @EqualsAndHashCode.Include
    private String identificationNumber;

    @EqualsAndHashCode.Exclude
    private String abbreviation;

    @EqualsAndHashCode.Exclude
    private String pictureUrl;

    @EqualsAndHashCode.Exclude
    private String description;

    @Column(unique = true, nullable = false)
    @EqualsAndHashCode.Exclude
    private String email;

    @OneToOne(targetEntity = UserAccount.class)
    @EqualsAndHashCode.Exclude
    private UserAccount userAccount;

    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "clinic_d", referencedColumnName = "id", nullable = false)
    private Clinic workplace;

    @EqualsAndHashCode.Exclude
    @ManyToOne(optional = false)
    @JoinColumn(name = "specialty_id", referencedColumnName = "id")
    private Specialty specialty;

    @EqualsAndHashCode.Exclude
    @ManyToMany
    @JoinTable(
            name = "physicians_patients",
            joinColumns = @JoinColumn(name = "physician_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "patient_id", referencedColumnName = "id")
    )
    private List<Patient> patients;

    @EqualsAndHashCode.Exclude
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "physicians_schedules",
            joinColumns = @JoinColumn(name = "physician_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "schedule_id", referencedColumnName = "id")
    )
    private List<DailySchedule> schedules;

}

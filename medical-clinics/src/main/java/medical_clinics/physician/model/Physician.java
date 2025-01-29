package medical_clinics.physician.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    private UUID id;

    @Basic(optional = false)
    private String firstName;

    @Basic(optional = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String identificationNumber;

    private String abbreviation;

    private String pictureUrl;

    private String description;

    @OneToOne(targetEntity = UserAccount.class)
    private UserAccount userAccount;

    @ManyToOne
    @JoinColumn(name = "clinic_d", referencedColumnName = "id", nullable = false)
    private Clinic workplace;

    @ManyToOne(optional = false)
    @JoinColumn(name = "specialty_id", referencedColumnName = "id")
    private Specialty specialty;

    @ManyToMany
    @JoinTable(
            name = "physicians_patients",
            joinColumns = @JoinColumn(name = "physician_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "patient_id", referencedColumnName = "id")
    )
    private List<Patient> patients;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "physicians_schedules",
            joinColumns = @JoinColumn(name = "physician_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "schedule_id", referencedColumnName = "id")
    )
    private List<DailySchedule> schedules;
}

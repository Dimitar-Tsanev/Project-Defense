package medical_clinics.patient.model;

import jakarta.persistence.*;
import lombok.*;
import medical_clinics.schedule.models.TimeSlot;
import medical_clinics.user_account.model.UserAccount;

import java.util.Collection;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter

@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(name = "UniquePhoneAndEmail", columnNames = {"email", "phone"})
)
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Basic(optional = false)
    private String firstName;

    @Basic(optional = false)
    private String lastName;

    private String identificationCode;

    private String country;

    private String city;

    private String address;

    private String phone;

    private String email;

    @OneToOne(targetEntity = UserAccount.class)
    private UserAccount userAccount;

    @OneToMany(mappedBy = "patient", targetEntity = TimeSlot.class)
    private Collection<TimeSlot> appointments;
}

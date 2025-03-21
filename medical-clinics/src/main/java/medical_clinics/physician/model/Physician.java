package medical_clinics.physician.model;

import jakarta.persistence.*;
import lombok.*;
import medical_clinics.clinic.models.Clinic;
import medical_clinics.specialty.model.Specialty;
import medical_clinics.user_account.model.UserAccount;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter

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

    @Column(unique = true, nullable = false)
    private String email;

    @OneToOne(targetEntity = UserAccount.class)
    private UserAccount userAccount;

    @ManyToOne
    @JoinColumn
    private Clinic workplace;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Specialty specialty;
}

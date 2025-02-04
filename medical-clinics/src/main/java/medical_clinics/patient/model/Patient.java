package medical_clinics.patient.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import medical_clinics.medical_record_note.model.MedicalRecordNote;
import medical_clinics.schedule.models.TimeSlot;
import medical_clinics.user_account.model.UserAccount;

import java.util.Collection;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data

@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(name = "UniquePhoneAndEmail", columnNames = {"phone", "email"})
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

    @OneToMany(mappedBy = "patient", targetEntity = MedicalRecordNote.class)
    private Collection<MedicalRecordNote> medicalRecord;

    @OneToMany(mappedBy = "patient", targetEntity = TimeSlot.class)
    private Collection<TimeSlot> appointments;
}

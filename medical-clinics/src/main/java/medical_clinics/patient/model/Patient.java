package medical_clinics.patient.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import medical_clinics.medical_record_note.model.MedicalRecordNote;
import medical_clinics.schedule.models.TimeSlot;
import medical_clinics.user_account.model.UserAccount;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data

@Entity
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Basic(optional = false)
    private String firstName;

    @Basic(optional = false)
    private String lastName;

    private String country;

    private String city;

    private String address;

    private String phone;

    @OneToOne(targetEntity = UserAccount.class)
    private UserAccount userAccount;

    @OneToMany(mappedBy = "patient", targetEntity = MedicalRecordNote.class)
    private List<MedicalRecordNote> medicalRecord;

    @OneToMany (mappedBy = "patient", targetEntity = TimeSlot.class)
    private List<TimeSlot> appointments;
}

package medical_clinics.medical_record_note.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import medical_clinics.patient.model.Patient;
import medical_clinics.physician.model.Physician;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data

@Entity
public class MedicalRecordNote {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String documentNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private Patient patient;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private Physician physician;

    @Basic(optional = false)
    private String diagnosis;

    private String diagnosisCode;

    @Basic(optional = false)
    private String chiefComplaint;

    @Basic(optional = false)
    private String MedicalHistory;

    @Basic(optional = false)
    private String examination;

    private String medicationAndRecommendations;

    private String testResults;
}

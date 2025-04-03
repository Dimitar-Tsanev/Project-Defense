package medical_record.note.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data

@Entity
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String documentNumber;

    @Basic(optional = false)
    private LocalDate creationDate;

    @Basic(optional = false)
    private String clinicIdentificationNumber;

    @Basic(optional = false)
    private UUID patientId;

    @Basic(optional = false)
    private UUID physicianId;

    @Basic(optional = false)
    private String diagnosis;

    private String diagnosisCode;

    private String chiefComplaint;

    @Basic(optional = false)
    private String medicalHistory;

    @Basic(optional = false)
    private String examination;

    private String medicationAndRecommendations;

    private String testResults;
}

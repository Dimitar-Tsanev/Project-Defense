package medical_clinics.records.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter

public class NoteDto {

    private UUID noteId;

    private String documentNumber;

    private String clinicIdentificationNumber;

    private LocalDate creationDate;

    private UUID patientId;

    private UUID physicianId;

    private String diagnosis;

    private String diagnosisCode;

    private String chiefComplaint;

    private String medicalHistory;

    private String examination;

    private String medicationAndRecommendations;

    private String testResults;
}

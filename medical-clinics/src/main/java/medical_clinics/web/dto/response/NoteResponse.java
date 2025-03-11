package medical_clinics.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter

public class NoteResponse {
    private String documentNumber;

    private LocalDate creationDate;

    private String clinicIdentificationNumber;

    private String physicianIdentificationNumber;

    private String physicianInfo;

    private String patientName;

    private String patientFullAddress;

    private String patientIdentificationCode;

    private String diagnosis;

    private String diagnosisCode;

    private String chiefComplaint;

    private String medicalHistory;

    private String examination;

    private String medicationAndRecommendations;

    private String testResults;
}

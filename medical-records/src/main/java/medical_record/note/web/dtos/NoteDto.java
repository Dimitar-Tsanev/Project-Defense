package medical_record.note.web.dtos;

import jakarta.validation.constraints.*;
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

    @NotBlank
    private String documentNumber;

    @NotNull
    @PastOrPresent
    private LocalDate creationDate;

    @NotBlank
    private String clinicIdentificationNumber;

    @NotNull
    @org.hibernate.validator.constraints.UUID
    private UUID patientId;

    @NotNull
    @org.hibernate.validator.constraints.UUID
    private UUID physicianId;

    @NotBlank
    @Size(min = 5, max = 255)
    @Pattern(regexp = "[A-Za-z0-9,.\\- _]")
    private String diagnosis;

    @Pattern(regexp = "^[A-Z]\\d{2}\\.\\d$")
    private String diagnosisCode;

    private String chiefComplaint;

    @NotBlank
    private String medicalHistory;

    @NotBlank
    private String examination;

    private String medicationAndRecommendations;

    private String testResults;
}

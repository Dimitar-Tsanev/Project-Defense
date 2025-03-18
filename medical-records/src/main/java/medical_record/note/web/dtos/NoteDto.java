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
    private UUID patientId;

    @NotNull
    private UUID physicianId;

    @NotBlank
    @Size(min = 5, max = 255, message = "Diagnosis must be between 5 and 255 character long")
    @Pattern(
            regexp = "[A-Za-z0-9,.\\-_\\s]+",
            message = "Diagnosis may contain: hyphens, commas, dots, digits, spaces, upper and lower case latin letters"
    )
    private String diagnosis;

    @Pattern(
            regexp = "^[A-Z]\\d{2}\\.\\d$",
            message = "Diagnosis code must follow the pattern uppercase latin letter two digits dot digit. Example - F30.9"
    )
    private String diagnosisCode;

    private String chiefComplaint;

    @NotBlank
    private String medicalHistory;

    @NotBlank
    private String examination;

    private String medicationAndRecommendations;

    private String testResults;
}

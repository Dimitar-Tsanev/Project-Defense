package medical_clinics.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter

public class NewNoteRequest {

    @NotBlank(message = "{not.blank}")
    @Size(min = 5, max = 255, message = "{diagnosis.length}")
    @Pattern(regexp = "[A-Za-z0-9,.\\-_\\s]+", message = "{diagnosis.unsupported.characters}")
    private String diagnosis;

    @Pattern(regexp = "^[A-Z]\\d{2}\\.\\d$", message = "{diagnosis.code}")
    private String diagnosisCode;

    private String chiefComplaint;

    @NotBlank(message = "{not.blank}")
    private String medicalHistory;

    @NotBlank(message = "{not.blank}")
    private String examination;

    private String medicationAndRecommendations;

    private String testResults;
}

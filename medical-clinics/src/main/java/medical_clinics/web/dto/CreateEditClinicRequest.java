package medical_clinics.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data

public class CreateEditClinicRequest {
    @NotBlank(message = "{not.blank}")
    private String city;

    @NotBlank(message = "{not.blank}")
    private String address;

    @NotEmpty(message = "{clinic.workdays.empty}")
    private List<WorkDayDto> workingDays;

    @NotEmpty(message = "{not.blank}")
    private String description;

    @NotBlank(message = "{not.blank}")
    @Size(min = 4, max = 21, message = "{phone.length.length}")
    @Pattern(regexp = "^[+]?\\d+$", message = "{phone.unsupported.characters}")
    private String phoneNumber;

    @NotBlank(message = "{not.blank}")
    private String identificationNumber;

    @NotBlank(message = "{not.blank}")
    @URL(message = "{valid.URL}")
    private String pictureUrl;

}

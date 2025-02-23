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
    @NotBlank
    private String city;

    @NotBlank
    private String address;

    @NotEmpty
    private List<WorkDayDto> workingDays;

    @NotEmpty
    private String description;

    @NotBlank
    @Size(min = 4, max = 21, message = "{phone.length.length}")
    @Pattern(regexp = "^[+]*\\d+$", message = "{phone.unsuported.characters}")
    private String phoneNumber;

    @NotBlank
    private String identificationNumber;

    @NotBlank
    @URL
    private String pictureUrl;

}

package medical_clinics.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import medical_clinics.shared.validation.contact_form_presented_validator.AtLeastOneContactForm;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data

@AtLeastOneContactForm
public class CreateEditPatient {

    @NotBlank(message = "{not.blank}")
    @Size(min = 2, max = 30, message = "{name.length}")
    @Pattern(regexp = "[A-Za-z]+", message = "{name.unsuported.characters}")
    private String firstName;

    @NotBlank(message = "{not.blank}")
    @Size(min = 2, max = 30, message = "{name.length}")
    @Pattern(regexp = "[A-Za-z]+", message = "{name.unsuported.characters}")
    private String lastName;

    @NotBlank(message = "{not.blank}")
    @Size(min = 5, max = 18, message = "{identification.code.length}")
    @Pattern(regexp = "^[0-9A-Z]+([.-]*[0-9A-Z]+)+$", message = "{identification.code.unsuported.character}")
    private String identificationCode;

    @NotBlank(message = "{not.blank}")
    @Size(min = 4, max = 56, message = "{country.length}")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "{country.unsuported.characters}")
    private String country;

    private String city;

    private String address;

    @Size(min = 4, max = 21, message = "{phone.length.length}")
    @Pattern(regexp = "^[+]*\\d+$", message = "{phone.unsuported.characters}")
    private String phone;

    @Email(
            message = "{email.format.not.match}",
            regexp = "^[a-zA-Z0-9]+([._-][0-9a-zA-Z]+)*@[a-zA-Z0-9]+([.-][0-9a-zA-Z]+)*\\.[a-zA-Z]{2,}$"
    )
    private String email;
}

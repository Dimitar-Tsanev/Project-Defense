package medical_clinics.web.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import medical_clinics.web.validation.password_change.PasswordChange;
import medical_clinics.web.validation.password_validator.Password;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

@PasswordChange
public class UserAccountEditRequest {

    @NotNull(message = "{not.blank}")
    @org.hibernate.validator.constraints.UUID(message = "{UUID.valid}")
    private UUID id;

    @NotBlank(message = "{not.blank}")
    @Size(min = 2, max = 30, message = "{name.length}")
    @Pattern(regexp = "[A-Za-z]+", message = "{name.unsupported.characters}")
    private String firstName;

    @NotBlank(message = "{not.blank}")
    @Size(min = 2, max = 30, message = "{name.length}")
    @Pattern(regexp = "[A-Za-z]+", message = "{name.unsupported.characters}")
    private String lastName;

    @Size(min = 4, max = 56, message = "{country.length}")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "{country.unsupported.characters}")
    private String country;

    private String city;

    private String address;

    @Size(min = 4, max = 21, message = "{phone.length.length}")
    @Pattern(regexp = "^[+]?\\d+$", message = "{phone.unsupported.characters}")
    private String phone;

    @NotBlank(message = "{not.blank}")
    @Email(
            message = "{email.format.not.match}",
            regexp = "^[a-zA-Z0-9]+([._-][0-9a-zA-Z]+)*@[a-zA-Z0-9]+([.-][0-9a-zA-Z]+)*\\.[a-zA-Z]{2,}$"
    )
    private String email;

    @Size(min = 8, max = 20, message = "{password.length}")
    @Password(
            constraintDigit = true,
            constraintLowercase = true,
            constraintUppercase = true,
            constraintSpecialSymbol = true,
            message = "{password.not.match}"
    )
    private String oldPassword;

    @Size(min = 8, max = 20, message = "{password.length}")
    @Password(
            constraintDigit = true,
            constraintLowercase = true,
            constraintUppercase = true,
            constraintSpecialSymbol = true,
            message = "{password.not.match}"
    )
    private String newPassword;
}

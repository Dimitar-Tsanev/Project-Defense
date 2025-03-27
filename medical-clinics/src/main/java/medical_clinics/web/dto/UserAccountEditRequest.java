package medical_clinics.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import medical_clinics.web.validation.password_change.PasswordChange;
import medical_clinics.web.validation.password.Password;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter

@PasswordChange
public class UserAccountEditRequest {
    @NotNull(message = "{not.blank}")
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
    @Schema(
            type = "string",
            pattern = "^[a-zA-Z0-9]+([._-][0-9a-zA-Z]+)*@[a-zA-Z0-9]+([.-][0-9a-zA-Z]+)*\\.[a-zA-Z]{2,}$",
            example = "example@example.com"
    )
    @Email(
            message = "{email.format.not.match}",
            regexp = "^[a-zA-Z0-9]+([._-][0-9a-zA-Z]+)*@[a-zA-Z0-9]+([.-][0-9a-zA-Z]+)*\\.[a-zA-Z]{2,}$"
    )
    private String email;

    @Size(min = 8, max = 20, message = "{password.length}")
    @Schema(
            type = "string",
            pattern = "(?=\\S+$)(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[.!@#$%^&*()_+<>?])[A-Za-z\\d!@#$%%^&*()_+<>?.]*",
            example = "1aA.....", minLength = 8, maxLength = 20
    )
    @Password(
            constraintDigit = true,
            constraintLowercase = true,
            constraintUppercase = true,
            constraintSpecialSymbol = true,
            message = "{password.not.match}"
    )
    private String oldPassword;

    @Size(min = 8, max = 20, message = "{password.length}")
    @Schema(
            type = "string",
            pattern = "(?=\\S+$)(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[.!@#$%^&*()_+<>?])[A-Za-z\\d!@#$%%^&*()_+<>?.]*",
            example = "1aA....."
    )
    @Password(
            constraintDigit = true,
            constraintLowercase = true,
            constraintUppercase = true,
            constraintSpecialSymbol = true,
            message = "{password.not.match}"
    )
    private String newPassword;
}

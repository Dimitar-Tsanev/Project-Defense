package medical_clinics.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import medical_clinics.web.validation.password.Password;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder

public class LoginRequest {
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

    @NotBlank(message = "{not.blank}")
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
    private String password;
}

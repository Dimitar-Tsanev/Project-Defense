package medical_clinics.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import medical_clinics.shared.validation.password_validator.Password;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder

public class LoginRequest {
    @NotBlank(message = "{not.blank}")
    @Email(
            message = "{email.format.not.match}",
            regexp = "^[a-zA-Z0-9]+([._-][0-9a-zA-Z]+)*@[a-zA-Z0-9]+([.-][0-9a-zA-Z]+)*\\.[a-zA-Z]{2,}$"
    )
    private String email;

    @NotBlank(message = "{not.blank}")
    @Size(min = 8, max = 20, message = "{password.length}")
    @Password(
            constraintDigit = true,
            constraintLowercase = true,
            constraintUppercase = true,
            constraintSpecialSymbol = true,
            message = "{password.not.match}"
    )
    private String password;
}

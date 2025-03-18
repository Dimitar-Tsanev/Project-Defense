package medical_clinics.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import medical_clinics.specialty.model.SpecialtyName;
import org.hibernate.validator.constraints.URL;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter

public class CreatePhysician {

    @NotBlank(message = "{not.blank}")
    @Size(min = 2, max = 30, message = "{name.length}")
    @Pattern(regexp = "[A-Za-z]+", message = "{name.unsupported.characters}")
    private String firstName;

    @NotBlank(message = "{not.blank}")
    @Size(min = 2, max = 30, message = "{name.length}")
    @Pattern(regexp = "[A-Za-z]+", message = "{name.unsupported.characters}")
    private String lastName;

    @NotBlank(message = "{not.blank}")
    @Size(min = 5, max = 18, message = "{identification.code.length}")
    @Pattern(regexp = "^[0-9A-Z]+([.-]*[0-9A-Z]+)+$", message = "{identification.code.unsupported.character}")
    private String identificationNumber;

    private String abbreviation;

    @URL(message = "{valid.URL}")
    private String pictureUrl;

    private String description;

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
    private String workplaceCity;

    @NotBlank(message = "{not.blank}")
    private String workplaceAddress;

    @NotBlank(message = "{not.blank}")
    @Schema(
            type = "string",
            description = "Case insensitive if word separator is space dot or hyphens will be change automatically",
            implementation = SpecialtyName.class)
    private String specialty;
}

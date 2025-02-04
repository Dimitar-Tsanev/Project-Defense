package medical_clinics.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data

public class CreatePhysician {

    private String firstName;

    private String lastName;

    private String identificationNumber;

    private String abbreviation;

    private String pictureUrl;

    private String description;

    private String email;

    private String workplaceCity;

    private String workplaceAddress;

    private String specialty;

}

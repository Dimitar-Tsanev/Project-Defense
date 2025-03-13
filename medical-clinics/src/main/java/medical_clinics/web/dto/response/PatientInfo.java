package medical_clinics.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data

public class PatientInfo {
    private UUID id;

    private String firstName;

    private String lastName;

    private String country;

    private String identificationCode;

    private String city;

    private String address;

    private String phone;

    private String email;

}

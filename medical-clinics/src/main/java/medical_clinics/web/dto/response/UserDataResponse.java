package medical_clinics.web.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import medical_clinics.user_account.model.Role;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter

public class UserDataResponse {
    private UUID accountId;

    private Role role;

    private PatientInfo patientInfo;
}

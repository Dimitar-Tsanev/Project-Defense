package medical_clinics.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import medical_clinics.user_account.model.Role;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter

public class UserDataResponse {
    private UUID accountId;

    private Role role;

    private PatientInfo patientInfo;
}

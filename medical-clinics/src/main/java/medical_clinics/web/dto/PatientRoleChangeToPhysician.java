package medical_clinics.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import medical_clinics.user_account.model.UserAccount;

@AllArgsConstructor
@Getter
public class PatientRoleChangeToPhysician {
    private UserAccount userAccount;
}

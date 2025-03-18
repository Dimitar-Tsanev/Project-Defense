package medical_clinics.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import medical_clinics.user_account.model.Role;
import medical_clinics.user_account.model.UserStatus;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter

public class AccountInformation {
    private UUID id;

    private String email;

    private Role role;

    private UserStatus status;
}

package medical_clinics.web.dto.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import medical_clinics.user_account.model.Role;

import java.util.UUID;

@AllArgsConstructor
@Getter

public class DemoteAccountEvent {
    private UUID accountId;

    private Role role;
}

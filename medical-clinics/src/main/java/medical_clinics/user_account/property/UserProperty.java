package medical_clinics.user_account.property;

import lombok.Builder;
import lombok.Getter;
import medical_clinics.user_account.model.Role;
import medical_clinics.user_account.model.UserStatus;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Builder

@ConfigurationProperties("user.account.default")
public class UserProperty {

    private Role role;

    private UserStatus status;

    private boolean messagingBlocked;
}

package medical_clinics.user_account.property;

import lombok.*;
import medical_clinics.user_account.model.Role;
import medical_clinics.user_account.model.UserStatus;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter

@ConfigurationProperties("user.account.default")
public class UserProperty {

    private Role role;

    private UserStatus status;
}

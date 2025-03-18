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

@ConfigurationProperties("user.account")
public class UserProperty {

    private Role defaultRole;

    private UserStatus status;

    private String adminMail;

    private String adminPassword;
}

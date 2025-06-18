package medical_clinics.user_account.property;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter

@ConfigurationProperties ("admin.default")
public class InitializedAdmin {
    private String mail;

    private String password;

    private String firstName;

    private String lastName;

    private String phone;
}

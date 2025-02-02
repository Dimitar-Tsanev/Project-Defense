package medical_clinics.user_account.mapper;

import medical_clinics.user_account.model.UserAccount;
import medical_clinics.user_account.property.UserProperty;
import medical_clinics.web.dto.RegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserAccountMapper {
    private UserAccountMapper () {
    }

    public static UserAccount registrationMapper (
            RegisterRequest registerRequest,
            UserProperty userProperty,
            PasswordEncoder passwordEncoder
    ) {
        return UserAccount.builder ( ).
                email ( registerRequest.getEmail ( ) ).
                password ( passwordEncoder.encode ( registerRequest.getPassword ( ) ) ).
                role ( userProperty.getRole ( ) ).
                status ( userProperty.getStatus ( ) ).
                isMessagingBlocked ( userProperty.isMessagingBlocked ( ) ).
                build ( );
    }
}

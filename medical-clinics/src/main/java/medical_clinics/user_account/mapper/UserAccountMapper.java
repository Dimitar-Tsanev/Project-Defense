package medical_clinics.user_account.mapper;

import medical_clinics.shared.security.UserDetailsImpl;
import medical_clinics.user_account.model.UserAccount;
import medical_clinics.user_account.property.UserProperty;
import medical_clinics.web.dto.RegisterRequest;
import medical_clinics.web.dto.UserAccountEditRequest;
import medical_clinics.web.dto.events.EditedAccountEvent;
import medical_clinics.web.dto.events.NewUserAccountEvent;
import medical_clinics.web.dto.response.AccountInformation;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserAccountMapper {
    private UserAccountMapper () {
    }

    public static UserAccount registrationMapper (
            RegisterRequest registerRequest,
            PasswordEncoder passwordEncoder,
            UserProperty userProperty ) {

        return UserAccount.builder ( ).
                email ( registerRequest.getEmail ( ) ).
                password ( passwordEncoder.encode ( registerRequest.getPassword ( ) ) ).
                role ( userProperty.getRole ( ) ).
                status ( userProperty.getStatus ( ) ).
                build ( );
    }

    public static NewUserAccountEvent mapToNewUserAccountEvent (
            UserAccount userAccount,
            String firstName,
            String lastName,
            String phone
    ) {
        return new NewUserAccountEvent ( userAccount, firstName, lastName, phone );
    }

    public static UserDetailsImpl mapToUserDetails ( UserAccount userAccount ) {
        return UserDetailsImpl.builder ( )
                .userId ( userAccount.getId ( ) )
                .email ( userAccount.getEmail ( ) )
                .password ( userAccount.getPassword ( ) )
                .role ( userAccount.getRole ( ) )
                .status ( userAccount.getStatus ( ) )
                .build ( );
    }

    public static EditedAccountEvent mapToEditedAccountEvent ( UserAccountEditRequest accountEdit, String oldEmail ) {
        return EditedAccountEvent.builder ( )
                .AccountId ( accountEdit.getId ( ) )
                .firstName ( accountEdit.getFirstName ( ) )
                .lastName ( accountEdit.getLastName ( ) )
                .country ( accountEdit.getCountry ( ) )
                .city ( accountEdit.getCity ( ) )
                .address ( accountEdit.getAddress ( ) )
                .phone ( accountEdit.getPhone ( ) )
                .newEmail ( accountEdit.getEmail ( ) )
                .oldEmail ( oldEmail )
                .build ( );
    }

    public static AccountInformation mapToAccountInformation ( UserAccount userAccount ) {
        return AccountInformation.builder ( )
                .id ( userAccount.getId ( ) )
                .email ( userAccount.getEmail ( ) )
                .status ( userAccount.getStatus ( ).name ( ) )
                .role ( userAccount.getRole ( ).name ( ) )
                .build ( );
    }
}

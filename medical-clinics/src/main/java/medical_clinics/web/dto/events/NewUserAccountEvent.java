package medical_clinics.web.dto.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import medical_clinics.user_account.model.UserAccount;

@AllArgsConstructor
@Getter

public class NewUserAccountEvent {

    private UserAccount userAccount;

    private String firstName;

    private String lastName;

    private String phoneNumber;
}

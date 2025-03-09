package medical_clinics.web.dto.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter

public class EditedAccountEvent {
    private UUID AccountId;

    private String firstName;

    private String lastName;

    private String country;

    private String city;

    private String address;

    private String phone;

    private String newEmail;

    private String oldEmail;
}

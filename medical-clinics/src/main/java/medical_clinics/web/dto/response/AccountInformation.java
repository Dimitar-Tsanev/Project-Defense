package medical_clinics.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter

public class AccountInformation {
    private UUID id;

    private String email;

    private String role;

    private String status;
}

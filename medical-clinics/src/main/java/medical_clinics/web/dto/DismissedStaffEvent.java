package medical_clinics.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter

public class DismissedStaffEvent {
    private UUID userAccountId;
}

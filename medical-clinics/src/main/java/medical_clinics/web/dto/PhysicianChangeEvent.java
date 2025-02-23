package medical_clinics.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor

public class PhysicianChangeEvent {

    private String firstName;

    private String lastName;

    private String oldEmail;

    private String newEmail;
}

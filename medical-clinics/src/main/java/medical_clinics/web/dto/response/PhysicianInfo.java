package medical_clinics.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder

public class PhysicianInfo {
    private UUID physicianId;

    private String firstName;

    private String lastName;

    private String abbreviation;

    private String pictureUrl;

    private String description;

    private String workplace;

    private String specialty;
}

package medical_clinics.web.dto;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder

public class PhysicianShortInfo {
    private UUID id;

    private String firstName;

    private String lastName;

    private String abbreviation;

    private String pictureUrl;

    private String description;

    private String workplace;

    private String specialty;
}

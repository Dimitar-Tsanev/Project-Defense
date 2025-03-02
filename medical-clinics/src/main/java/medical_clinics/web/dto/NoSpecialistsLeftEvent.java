package medical_clinics.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter

public class NoSpecialistsLeftEvent {
    UUID clinicId;

    UUID specialtyId;
}

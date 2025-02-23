package medical_clinics.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import medical_clinics.physician.model.Physician;

@AllArgsConstructor
@Getter
public class NewPhysicianEvent {
    private Physician physician;
}

package medical_clinics.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data

public class ClinicDetails {

    private UUID id;

    private String city;

    private String address;

    private String pictureUrl;

    private String description;

    private String phoneNumber;

    private Collection<WorkDayDto> workingDays;

    private Collection<SpecialityDto> specialties;
}

package medical_clinics.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data

public class CreateEditClinicRequest {
    private String city;

    private String address;

    private List<WorkDayDto> workingDays;

    private String description;

    private String phoneNumber;

    private String identificationNumber;

    private String pictureUrl;

}

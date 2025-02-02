package medical_clinics.clinic.mappers;

import medical_clinics.clinic.models.Clinic;
import medical_clinics.specialty.mapper.SpecialityMapper;
import medical_clinics.web.dto.ClinicDetails;
import medical_clinics.web.dto.ClinicShortInfo;
import medical_clinics.web.dto.NewClinicRequest;

import java.util.stream.Collectors;

public class ClinicMapper {
    private ClinicMapper() {}

    public static ClinicShortInfo mapClinicToClinicShortInfo(Clinic clinic) {
        return ClinicShortInfo.builder ()
                .id ( clinic.getId () )
                .city ( clinic.getCity() )
                .address ( clinic.getAddress() )
                .pictureUrl ( clinic.getPictureUrl() )
                .build();
    }

    public static ClinicDetails mapClinicToClinicDetails(Clinic clinic) {
        return ClinicDetails.builder ( )
                .id ( clinic.getId () )
                .city ( clinic.getCity() )
                .address ( clinic.getAddress() )
                .pictureUrl ( clinic.getPictureUrl() )
                .phoneNumber ( clinic.getPhoneNumber() )
                .description ( clinic.getDescription() )
                .workingDays (
                        clinic.getWorkingDays().stream( ).map ( WorkDayMapper::mapToDto ).collect( Collectors.toSet () )
                )
                .specialties (
                        clinic.getSpecialtyList ().stream( ).map ( SpecialityMapper::mapToDto ).collect( Collectors.toSet ())
                )
                .build ( );
    }

    public static Clinic mapToClinic ( NewClinicRequest clinic ) {
        return Clinic.builder ( )
                .city ( clinic.getCity ( ) )
                .address ( clinic.getAddress ( ) )
                .pictureUrl ( clinic.getPictureUrl ( ) )
                .phoneNumber ( clinic.getPhoneNumber ( ) )
                .description ( clinic.getDescription ( ) )
                .identificationNumber ( clinic.getIdentificationNumber ( ) )
                .workingDays (
                        clinic.getWorkingDays ().stream( ).map ( WorkDayMapper::mapToModel ).collect( Collectors.toSet () )
                )
                .build ( );
    }
}

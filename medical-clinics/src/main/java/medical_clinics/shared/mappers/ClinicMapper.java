package medical_clinics.shared.mappers;

import medical_clinics.clinic.models.Clinic;
import medical_clinics.web.dto.ClinicDetails;
import medical_clinics.web.dto.ClinicShortInfo;
import medical_clinics.web.dto.CreateEditClinicRequest;

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
                        clinic.getSpecialties ().stream( ).map ( SpecialityMapper::mapToDto ).collect( Collectors.toSet ())
                )
                .build ( );
    }

    public static Clinic mapToClinic ( CreateEditClinicRequest clinic ) {
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

package medical_clinics.specialty.mapper;

import medical_clinics.specialty.model.Specialty;
import medical_clinics.specialty.model.SpecialtyName;
import medical_clinics.web.dto.response.SpecialityDto;

public class SpecialityMapper {
    private SpecialityMapper () {
    }

    public static SpecialityDto mapToDto ( Specialty specialty ) {
        return SpecialityDto.builder ( )
                .id ( specialty.getId ( ) )
                .name ( specialty.getName ( ).toString ( ) )
                .build ( );
    }

    public static SpecialtyName mapNameFromString ( String specialtyName ) {
        return SpecialtyName.valueOf (
                specialtyName.toUpperCase ( ).replaceAll ( "[\\s-.]", "_" )
        );
    }
}

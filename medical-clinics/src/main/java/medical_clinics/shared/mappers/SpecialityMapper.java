package medical_clinics.shared.mappers;

import medical_clinics.specialty.model.Specialty;
import medical_clinics.specialty.model.SpecialtyName;
import medical_clinics.web.dto.SpecialityDto;

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

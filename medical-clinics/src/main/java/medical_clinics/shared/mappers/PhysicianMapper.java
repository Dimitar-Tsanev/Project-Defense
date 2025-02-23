package medical_clinics.shared.mappers;

import medical_clinics.physician.model.Physician;
import medical_clinics.web.dto.CreatePhysician;

public class PhysicianMapper {
    private PhysicianMapper() {}

    public static Physician mapFromCreate( CreatePhysician createPhysician) {
        return Physician.builder ( )
                .firstName ( createPhysician.getFirstName ( ) )
                .lastName ( createPhysician.getLastName ( ) )
                .abbreviation ( createPhysician.getAbbreviation ( ) )
                .email ( createPhysician.getEmail ( ) )
                .pictureUrl ( createPhysician.getPictureUrl ( ) )
                .description ( createPhysician.getDescription ( ) )
                .identificationNumber ( createPhysician.getIdentificationNumber ( ) )
                .build ( );
    }

}

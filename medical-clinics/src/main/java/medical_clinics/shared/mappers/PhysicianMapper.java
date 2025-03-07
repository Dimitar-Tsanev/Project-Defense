package medical_clinics.shared.mappers;

import medical_clinics.physician.model.Physician;
import medical_clinics.web.dto.CreatePhysician;
import medical_clinics.web.dto.PhysicianShortInfo;

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

    public static PhysicianShortInfo mapToPhysicianShortInfo( Physician physician ) {
        String workplaceAddress = physician.getWorkplace ().getCity () +
                ", " +
                physician.getWorkplace ( ).getAddress ();

        String specialtyName = physician.getSpecialty ().getName ().name ();
        String specialtyNameFormatted = specialtyName.charAt (0 ) +
                specialtyName.substring (1).toLowerCase (  );

        return PhysicianShortInfo.builder ( )
                .id ( physician.getId ( ) )
                .firstName ( physician.getFirstName ( ) )
                .lastName ( physician.getLastName ( ) )
                .abbreviation ( physician.getAbbreviation ( ) )
                .pictureUrl ( physician.getPictureUrl ( ) )
                .description ( physician.getDescription ( ) )
                .workplace ( workplaceAddress )
                .specialty ( specialtyNameFormatted )
                .build ( );
    }
}

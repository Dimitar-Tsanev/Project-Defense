package medical_clinics.patient.mapper;

import medical_clinics.patient.model.Patient;
import medical_clinics.web.dto.RegisterRequest;

public class PatientMapper {
    private PatientMapper () {
    }

    public static Patient mapFromRegistrationRequest ( RegisterRequest registerRequest ) {
        return Patient.builder ( )
                .firstName ( registerRequest.getFirstName ( ) )
                .lastName ( registerRequest.getLastName ( ) )
                .phone ( registerRequest.getPhone ( ) )
                .build ( );
    }
}

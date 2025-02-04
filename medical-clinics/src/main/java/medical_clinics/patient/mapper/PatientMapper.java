package medical_clinics.patient.mapper;

import medical_clinics.patient.model.Patient;
import medical_clinics.web.dto.CreateEditPatient;
import medical_clinics.web.dto.PatientInfo;
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

    public static Patient mapFromCreateEditPatient ( CreateEditPatient createEditPatient ) {
        return Patient.builder ()
                .firstName ( createEditPatient.getFirstName ( ) )
                .lastName ( createEditPatient.getLastName ( ) )
                .identificationCode ( createEditPatient.getIdentificationCode ( ) )
                .country ( createEditPatient.getCountry ( ) )
                .city ( createEditPatient.getCity ( ) )
                .address ( createEditPatient.getAddress ( ) )
                .phone ( createEditPatient.getPhone ( ) )
                .email ( createEditPatient.getEmail ( ) )
                .build ();
    }

    public static PatientInfo mapToPatientInfo ( Patient patient ) {
        return PatientInfo.builder ( )
                .id ( patient.getId ( ) )
                .firstName ( patient.getFirstName ( ) )
                .lastName ( patient.getLastName ( ) )
                .country ( patient.getCountry ( ) )
                .city ( patient.getCity ( ) )
                .address ( patient.getAddress ( ) )
                .identificationCode ( patient.getIdentificationCode ( ) )
                .phone ( patient.getPhone ( ) )
                .email ( patient.getEmail ( ) )
                .build ( );
    }
}

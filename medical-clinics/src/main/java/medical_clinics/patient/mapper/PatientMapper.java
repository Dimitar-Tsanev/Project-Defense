package medical_clinics.patient.mapper;

import medical_clinics.patient.model.Patient;
import medical_clinics.web.dto.CreatePatient;
import medical_clinics.web.dto.events.NewUserAccountEvent;
import medical_clinics.web.dto.response.PatientInfo;

public class PatientMapper {
    private PatientMapper () {
    }

    public static Patient mapFromNewUserAccount ( NewUserAccountEvent newUserAccount ) {
        return Patient.builder ( )
                .firstName ( newUserAccount.getFirstName ( ) )
                .lastName ( newUserAccount.getLastName ( ) )
                .email ( newUserAccount.getUserAccount ( ).getEmail ( ) )
                .phone ( newUserAccount.getPhoneNumber ( ) )
                .build ( );
    }

    public static Patient mapFromCreateEditPatient ( CreatePatient createPatient ) {
        return Patient.builder ( )
                .firstName ( createPatient.getFirstName ( ) )
                .lastName ( createPatient.getLastName ( ) )
                .identificationCode ( createPatient.getIdentificationCode ( ) )
                .country ( createPatient.getCountry ( ) )
                .city ( createPatient.getCity ( ) )
                .address ( createPatient.getAddress ( ) )
                .phone ( createPatient.getPhone ( ) )
                .email ( createPatient.getEmail ( ) )
                .build ( );
    }

    public static PatientInfo mapToPatientInfo ( Patient patient ) {
        if ( patient == null ) {
            return null;
        }
        return PatientInfo.builder ( )
                .patientId ( patient.getId ( ) )
                .firstName ( patient.getFirstName ( ) )
                .lastName ( patient.getLastName ( ) )
                .country ( patient.getCountry ( ) )
                .identificationCode ( patient.getIdentificationCode ( ) )
                .city ( patient.getCity ( ) )
                .address ( patient.getAddress ( ) )
                .phone ( patient.getPhone ( ) )
                .email ( patient.getEmail ( ) )
                .build ( );
    }
}

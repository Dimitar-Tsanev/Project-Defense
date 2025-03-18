package medical_clinics.web.validation.new_note_patient;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import medical_clinics.patient.model.Patient;
import medical_clinics.patient.service.PatientService;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.UUID;

@AllArgsConstructor

@Component
public class NewNoteConstrainPatientNecessaryInformationValidator
        implements ConstraintValidator<NewNoteConstrainPatientNecessaryInformation, UUID> {

    private final PatientService patientService;

    private static String message;
    private static boolean isMessageEmpty;

    @Override
    public void initialize ( NewNoteConstrainPatientNecessaryInformation constraint ) {
        if ( message == null || message.isBlank ( ) ) {
            isMessageEmpty = true;
            message = "Edit patient first.";

        } else {
            message = constraint.message ( );
            isMessageEmpty = false;
        }
        ConstraintValidator.super.initialize ( constraint );
    }

    @Override
    public boolean isValid ( UUID value, ConstraintValidatorContext context ) {
        Patient patient = patientService.getPatientById ( UUID.fromString ( value.toString ( ) ) );

        String country = patient.getCountry ( );
        String identificationCode = patient.getIdentificationCode ( );

        if ( country != null && identificationCode != null ) {
            return true;
        }

        if ( identificationCode != null ) {
            applyMessage ( "Patient country is missing", context );
            return false;
        }

        if ( country != null ) {
            applyMessage ( "Patient identification code is missing", context );
            return false;
        }

        applyMessage ( "Patient country and identification code is missing", context );
        return false;
    }

    private void applyMessage ( String messageBuilder, ConstraintValidatorContext context ) {
        if ( isMessageEmpty ) {
            message = message + " " + messageBuilder;
        }

        context.unwrap ( HibernateConstraintValidatorContext.class )
                .buildConstraintViolationWithTemplate ( message )
                .addConstraintViolation ( )
                .disableDefaultConstraintViolation ( );

    }
}

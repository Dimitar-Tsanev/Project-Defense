package medical_clinics.web.validation.new_note_patient;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import medical_clinics.patient.model.Patient;
import medical_clinics.patient.service.PatientService;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import java.util.UUID;

@AllArgsConstructor

public class NewNoteConstrainPatientNecessaryInformationValidator
        implements ConstraintValidator<NewNoteConstrainPatientNecessaryInformation, CharSequence> {

    private final PatientService patientService;

    private String message;
    private boolean isMessageEmpty;

    @Override
    public void initialize ( NewNoteConstrainPatientNecessaryInformation constraint ) {
        if ( message.isBlank ( ) ) {
            isMessageEmpty = true;
            this.message = "Edit patient first.";

        } else {
            this.message = constraint.message ( );
            isMessageEmpty = false;
        }
        ConstraintValidator.super.initialize ( constraint );
    }

    @Override
    public boolean isValid ( CharSequence value, ConstraintValidatorContext context ) {
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

    private void applyMessage ( String message, ConstraintValidatorContext context ) {
        if ( isMessageEmpty ) {
            this.message = this.message + " " + message;
        }

        context.unwrap ( HibernateConstraintValidatorContext.class )
                .buildConstraintViolationWithTemplate ( message )
                .addConstraintViolation ( )
                .disableDefaultConstraintViolation ( );

    }
}

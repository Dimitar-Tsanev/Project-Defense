package medical_clinics.web.validation.contact_form_present;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import medical_clinics.web.dto.CreatePatient;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

public class AtLeastOneContactFormValidator implements ConstraintValidator<AtLeastOneContactForm, CreatePatient> {

    private String message;

    @Override
    public void initialize ( AtLeastOneContactForm constraintAnnotation ) {
        message = constraintAnnotation.message ( );


        ConstraintValidator.super.initialize ( constraintAnnotation );
    }

    @Override
    public boolean isValid ( CreatePatient patient, ConstraintValidatorContext context ) {
        String email = patient.getEmail ( );
        String phone = patient.getPhone ( );

        if ( email == null && phone == null ) {
            context.unwrap ( HibernateConstraintValidatorContext.class )
                    .buildConstraintViolationWithTemplate ( message )
                    .addConstraintViolation ( )
                    .disableDefaultConstraintViolation ( );

            return false;
        }
        return true;
    }
}

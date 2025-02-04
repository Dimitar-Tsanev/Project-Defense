package medical_clinics.shared.validation.contact_form_presented_validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention ( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE)
@Constraint(validatedBy = AtLeastOneContactFormValidator.class)
public @interface AtLeastOneContactForm {

    String message() default "At lest one contact form is required";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

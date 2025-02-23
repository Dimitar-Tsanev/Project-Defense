package medical_clinics.shared.validation.password_change;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = PasswordChangeValidator.class)
public @interface PasswordChange {

    String message () default "";

    Class<?>[] groups () default {};

    Class<? extends Payload>[] payload () default {};

}

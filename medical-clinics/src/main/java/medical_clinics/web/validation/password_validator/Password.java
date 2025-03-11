package medical_clinics.web.validation.password_validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(
        validatedBy = {PasswordValidator.class}
)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {
    String message () default "";

    boolean constraintDigit () default false;

    boolean constraintLowercase () default false;

    boolean constraintUppercase () default false;

    boolean constraintSpecialSymbol () default false;

    Class<?>[] groups () default {};

    Class<? extends Payload>[] payload () default {};

}
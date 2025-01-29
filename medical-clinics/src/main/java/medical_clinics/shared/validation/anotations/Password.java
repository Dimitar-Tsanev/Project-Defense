package medical_clinics.shared.validation.anotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import medical_clinics.shared.validation.PasswordValidator;
import java.lang.annotation.*;

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
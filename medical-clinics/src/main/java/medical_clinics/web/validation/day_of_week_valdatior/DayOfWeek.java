package medical_clinics.web.validation.day_of_week_valdatior;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(
        validatedBy = {DayOfWeekValidator.class}
)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DayOfWeek {
    String message () default "";

    Class<?>[] groups () default {};

    Class<? extends Payload>[] payload () default {};

}

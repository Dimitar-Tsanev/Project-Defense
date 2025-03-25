package medical_clinics.web.validation.day_of_week;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import medical_clinics.clinic.models.DaysOfWeek;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;


public class DayOfWeekNameValidator implements ConstraintValidator<DayOfWeekName, CharSequence> {
    private String message;

    private boolean isMessageEmpty;

    @Override
    public void initialize ( DayOfWeekName constraint ) {
        if ( constraint.message ( ).isEmpty ( ) ) {
            isMessageEmpty = true;
        }
        message = constraint.message ( );

        ConstraintValidator.super.initialize ( constraint );
    }

    @Override
    public boolean isValid ( CharSequence value, ConstraintValidatorContext context ) {
        Set<String> daysOfWeek = Arrays.stream ( DaysOfWeek.values ( ) )
                .map ( Enum::name )
                .collect ( Collectors.toSet ( ) );

        if ( daysOfWeek.contains ( value.toString ( ).toUpperCase ( ) ) ) {
            return true;
        }

        if ( isMessageEmpty ) {
            this.message = "Invalid day of week: %s".formatted ( value );
        }

        context.unwrap ( HibernateConstraintValidatorContext.class )
                .buildConstraintViolationWithTemplate ( message )
                .addConstraintViolation ( )
                .disableDefaultConstraintViolation ( );

        return false;
    }
}

package medical_clinics.shared.validation.password_validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class PasswordValidator implements ConstraintValidator<Password, CharSequence> {
    private static final String PATTERN_TEMPLATE = "[A-Za-z\\d!@#$%%^&*()_+<>?.]*$";

    private static final String ONE_DIGIT = "(?=.*\\d)";
    private static final String LOWER_CASE = "(?=.*[a-z])";
    private static final String UPPER_CASE = "(?=.*[A-Z])";
    private static final String SPECIAL_CHAR = "(?=.*[.!@#$%^&*()_+<>?])";
    private static final String NO_SPACE = "(?=\\S+$)";

    private boolean constraintDigit;
    private boolean constraintLowercase;
    private boolean constraintUppercase;
    private boolean constraintSpecialSymbol;
    private String message;

    private boolean isMessageEmpty;
    private String buildMessage;

    @Override
    public void initialize ( Password constraint ) {
        this.constraintDigit = constraint.constraintDigit ( );
        this.constraintLowercase = constraint.constraintLowercase ( );
        this.constraintUppercase = constraint.constraintUppercase ( );
        this.constraintSpecialSymbol = constraint.constraintSpecialSymbol ( );
        this.message = constraint.message ( );

        ConstraintValidator.super.initialize ( constraint );
    }

    @Override
    public boolean isValid ( CharSequence value, ConstraintValidatorContext context ) {
        if ( message.isBlank ( ) ) {
            isMessageEmpty = true;
            message = "Password must not contain white spaces. And must contain at least: %s";

        } else {
            isMessageEmpty = false;
        }
        String pattern = getPatternBuilder ( ) + PATTERN_TEMPLATE;

        Pattern regex = Pattern.compile ( pattern );

        Matcher matcher = regex.matcher ( value );

        boolean isValid = matcher.matches ( );

        if ( !isValid ) {
            if ( isMessageEmpty && !buildMessage.isBlank ( ) ) {
                message = String.format ( message, buildMessage );

            } else if ( isMessageEmpty && buildMessage.isBlank ( ) ) {
                message = String.format ( message, "letter digit or one of this .!@#$%^&*()_+<>? characters " );
            }
            context.unwrap ( HibernateConstraintValidatorContext.class )
                    .buildConstraintViolationWithTemplate ( message )
                    .addConstraintViolation ( )
                    .disableDefaultConstraintViolation ( );
        }
        return isValid;
    }

    private StringBuilder getPatternBuilder () {
        StringBuilder patternBuilder = new StringBuilder ( );

        patternBuilder.append ( "^" );
        patternBuilder.append ( NO_SPACE );

        if ( isMessageEmpty ) {
            buildMessage = "";
        }
        if ( this.constraintDigit ) {
            patternBuilder.append ( ONE_DIGIT );
            buildMessage = "one digit, ";
        }
        if ( this.constraintLowercase ) {
            patternBuilder.append ( LOWER_CASE );
            buildMessage = buildMessage + "one lower case letter, ";
        }
        if ( this.constraintUppercase ) {
            patternBuilder.append ( UPPER_CASE );
            buildMessage = buildMessage + "one upper case letter, ";
        }
        if ( this.constraintSpecialSymbol ) {
            patternBuilder.append ( SPECIAL_CHAR );
            buildMessage = "one of the following characters: .!@#$%^&*()_+<>?  ";
        }
        buildMessage = buildMessage.substring ( 0, buildMessage.length ( ) - 2 );

        return patternBuilder;
    }
}

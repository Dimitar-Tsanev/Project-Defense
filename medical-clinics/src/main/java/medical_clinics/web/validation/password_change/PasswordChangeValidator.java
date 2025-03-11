package medical_clinics.web.validation.password_change;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import medical_clinics.user_account.model.UserAccount;
import medical_clinics.user_account.repository.UserAccountRepository;
import medical_clinics.web.dto.UserAccountEditRequest;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@AllArgsConstructor
public class PasswordChangeValidator implements ConstraintValidator<PasswordChange, UserAccountEditRequest> {

    private static final String BLANK_OLD_PASSWORD = "To change the password you must provide old password";
    private static final String INCORRECT_USER_ID = "Invalid user id";
    private static final String INCORRECT_PASSWORD = "Incorrect password";


    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    private String message;
    private boolean generateMessage;

    @Override
    public void initialize ( PasswordChange constraintAnnotation ) {
        if ( constraintAnnotation.message ( ).isEmpty ( ) ) {
            generateMessage = true;
        }
        message = constraintAnnotation.message ( );

        ConstraintValidator.super.initialize ( constraintAnnotation );
    }

    @Override
    public boolean isValid ( UserAccountEditRequest accountEdit, ConstraintValidatorContext context ) {
        String oldPassword = accountEdit.getOldPassword ( );
        String newPassword = accountEdit.getNewPassword ( );

        if ( oldPassword == null && newPassword == null ) {
            return true;
        }

        if ( oldPassword == null ) {
            return returnViolations ( context, BLANK_OLD_PASSWORD );
        }

        Optional<UserAccount> userAccountIfExist = userAccountRepository.findById ( accountEdit.getId ( ) );

        if ( userAccountIfExist.isEmpty ( ) ) {
            return returnViolations ( context, INCORRECT_USER_ID );
        }

        UserAccount userAccount = userAccountIfExist.get ( );

        if ( !passwordEncoder.matches ( oldPassword, userAccount.getPassword ( ) ) ) {
            return returnViolations ( context, INCORRECT_PASSWORD );
        }

        return true;
    }

    private boolean returnViolations ( ConstraintValidatorContext context, String message ) {
        if ( generateMessage ) {
            this.message = message;
        }

        context.unwrap ( HibernateConstraintValidatorContext.class )
                .buildConstraintViolationWithTemplate ( this.message )
                .addConstraintViolation ( )
                .disableDefaultConstraintViolation ( );

        return false;
    }
}

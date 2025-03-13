package medical_clinics.web.validation.new_note_patient;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(
        validatedBy = {NewNoteConstrainPatientNecessaryInformationValidator.class}
)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface NewNoteConstrainPatientNecessaryInformation {
    String message () default "";

    Class<?>[] groups () default {};

    Class<? extends Payload>[] payload () default {};
}

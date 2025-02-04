package medical_clinics.shared.exception;

import java.util.NoSuchElementException;

public class PatientNotFoundException extends NoSuchElementException {
  public PatientNotFoundException(String message) {
    super(message);
  }
}

package medical_clinics.web.exception_handler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import medical_clinics.clinic.exceptions.ExistingClinicException;
import medical_clinics.clinic.exceptions.NoSuchClinicException;
import medical_clinics.patient.exceptions.PatientAlreadyExistsException;
import medical_clinics.patient.exceptions.PatientNotFoundException;
import medical_clinics.physician.exceptions.PhysicianAlreadyExistException;
import medical_clinics.physician.exceptions.PhysicianNotFoundException;
import medical_clinics.records.exceptions.NoteException;
import medical_clinics.schedule.exceptions.ScheduleConflictException;
import medical_clinics.schedule.exceptions.ScheduleNotFoundException;
import medical_clinics.shared.exception.PersonalInformationDontMatchException;
import medical_clinics.specialty.exceptions.SpecialityException;
import medical_clinics.user_account.exceptions.UserAccountNotFoundException;
import medical_clinics.user_account.exceptions.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> handleConstraintViolationException ( ConstraintViolationException e ) {
        logException ( e );

        return ResponseEntity
                .status ( HttpStatus.BAD_REQUEST )
                .body (
                        ExceptionResponse.builder ( )
                                .errorCode ( HttpStatus.BAD_REQUEST.value ( ) )
                                .messages (
                                        e.getConstraintViolations ( )
                                                .stream ( )
                                                .map ( ConstraintViolation::getMessage )
                                                .collect ( Collectors.toList ( ) )
                                )
                                .build ( )
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException ( MethodArgumentNotValidException e ) {
        Map<String, List<String>> errors = new HashMap<> ( );
        e.getBindingResult ( ).getAllErrors ( )
                .forEach ( error -> {
                    String fieldName = ((FieldError) error).getField ( );
                    errors.putIfAbsent ( fieldName, new ArrayList<> ( ) );
                    errors.get ( fieldName ).add ( error.getDefaultMessage ( ) );
                } );

        List<String> fieldsErrors = errors.entrySet ( ).stream ( )
                .map ( entry -> entry.getKey ( ) + ": " + String.join ( ", ", entry.getValue ( )
                ) ).toList ( );

        logException ( e );

        return ResponseEntity
                .status ( HttpStatus.BAD_REQUEST )
                .body (
                        ExceptionResponse.builder ( )
                                .errorCode ( HttpStatus.BAD_REQUEST.value ( ) )
                                .messages ( fieldsErrors )
                                .build ( )
                );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleException ( BadCredentialsException e ) {
        BadCredentialsException ex = new BadCredentialsException ( "Invalid email or password", e );
        return buildResponseError ( HttpStatus.UNAUTHORIZED, ex );
    }

    @ExceptionHandler(NoteException.class)
    public ResponseEntity<ExceptionResponse> handleNoteException ( NoteException e ) {
        logException ( e );

        return ResponseEntity.status ( HttpStatus.valueOf ( e.getStatusCode ( ) ) ).body (
                ExceptionResponse.builder ( )
                        .errorCode ( e.getStatusCode ( ) )
                        .messages ( List.of ( e.getMessage ( ) ) )
                        .build ( )
        );
    }

    @ExceptionHandler({
            ExistingClinicException.class,
            PatientAlreadyExistsException.class,
            PersonalInformationDontMatchException.class,
            PhysicianAlreadyExistException.class,
            UserAlreadyExistsException.class,
            ScheduleConflictException.class
    })
    public ResponseEntity<ExceptionResponse> handleScheduleConflictException ( IllegalArgumentException e ) {
        return buildResponseError ( HttpStatus.CONFLICT, e );
    }

    @ExceptionHandler({
            NoSuchClinicException.class,
            PatientNotFoundException.class,
            PhysicianNotFoundException.class,
            SpecialityException.class,
            UserAccountNotFoundException.class,
            ScheduleNotFoundException.class,
    })
    public ResponseEntity<ExceptionResponse> handleNoSuchElementException ( NoSuchElementException e ) {
        return buildResponseError ( HttpStatus.NOT_FOUND, e );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleUndefinedException ( Exception e ) {
        log.error ( "Internal server error caused by: {} and message {}",
                e.getClass ( ).getName ( ), e.getMessage ( ) );

        return ResponseEntity.status ( HttpStatus.INTERNAL_SERVER_ERROR ).body (
                ExceptionResponse.builder ( )
                        .errorCode ( HttpStatus.INTERNAL_SERVER_ERROR.value ( ) )
                        .messages (
                                List.of ( "Internal server error caused by: {} and message {}",
                                        e.getClass ( ).getName ( ), e.getMessage ( ) )
                        )
                        .build ( )
        );
    }

    private ResponseEntity<ExceptionResponse> buildResponseError ( HttpStatus status, Exception e ) {
        logException ( e );
        return ResponseEntity
                .status ( status )
                .body ( ExceptionResponse.builder ( )
                        .errorCode ( status.value ( ) )
                        .messages ( List.of ( e.getMessage ( ) ) )
                        .build ( )
                );
    }

    private void logException ( Exception e ) {
        log.error ( "Exception occur cause: {} message: {}", e.getCause ( ).toString ( ), e.getMessage ( ) );
    }
}

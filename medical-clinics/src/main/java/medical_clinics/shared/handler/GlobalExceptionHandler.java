package medical_clinics.shared.handler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import medical_clinics.shared.exception.*;
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
        log.error ( e.getMessage ( ), e.getCause () );
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

        log.error ( e.getMessage ( ), e.getCause ( ) );

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
    public ResponseEntity<ExceptionResponse> handleException(BadCredentialsException e ) {
        BadCredentialsException ex = new BadCredentialsException ( "Invalid email or password", e.fillInStackTrace () );
        return buildResponseError ( HttpStatus.UNAUTHORIZED, ex );
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


    private ResponseEntity<ExceptionResponse> buildResponseError ( HttpStatus status, Exception e ) {
        log.error ( e.getMessage ( ), e.getCause () );
        return ResponseEntity
                .status ( status )
                .body ( ExceptionResponse.builder ( )
                        .errorCode ( status.value ( ) )
                        .messages ( List.of ( e.getMessage ( ) ) )
                        .build ( )
                );
    }
}

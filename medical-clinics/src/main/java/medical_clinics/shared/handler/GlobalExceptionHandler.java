package medical_clinics.shared.handler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> handleConstraintViolationException ( ConstraintViolationException e ) {
        return ResponseEntity
                .status ( HttpStatus.BAD_REQUEST )
                .body (
                        ExceptionResponse.builder ( )
                                .errorCode ( 400 )
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
        List<String> errors = new ArrayList<> ( );
        e.getBindingResult ( ).getAllErrors ( )
                .forEach ( error -> {
                    String errorMessage = error.getDefaultMessage ( );
                    errors.add ( errorMessage );
                } );

        return ResponseEntity
                .status ( HttpStatus.BAD_REQUEST )
                .body (
                        ExceptionResponse.builder ( )
                                .errorCode ( 400 )
                                .messages ( errors )
                                .build ( )
                );
    }
}

package medical_record.note.web.handler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import medical_record.note.util.NoteNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @ExceptionHandler(NoteNotFound.class)
    public ResponseEntity<ExceptionResponse> handleRuntimeException ( NoteNotFound e ) {
        logException ( e );

        return ResponseEntity
                .status ( HttpStatus.NOT_FOUND )
                .body ( ExceptionResponse.builder ( )
                        .errorCode ( HttpStatus.NOT_FOUND.value ( ) )
                        .messages ( List.of ( e.getMessage ( ) ) )
                        .build ( )
                );
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

    private void logException ( Exception e ) {
        log.error ( "Exception occur cause: {} message: {}", e.getClass ( ).getName ( ), e.getMessage ( ) );
    }
}

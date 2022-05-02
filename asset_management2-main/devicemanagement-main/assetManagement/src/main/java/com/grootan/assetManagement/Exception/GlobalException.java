package com.grootan.assetManagement.Exception;

import com.grootan.assetManagement.Model.ApiErrors;
import org.hibernate.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<Object> handleUserAlreadyExistException(UserAlreadyExistException exception)
    {
        String msg = exception.getMessage();
        List<String> details = new ArrayList<>();
        details.add("User Already Exists");
        HttpStatus status = HttpStatus.CONFLICT;
        ApiErrors errors = new ApiErrors(msg,details,status, LocalDateTime.now());
        return ResponseEntity.status(status).body(errors);
    }
    @ExceptionHandler(IdAlreadyException.class)
    public ResponseEntity<Object> handleIdAlreadyException(UserAlreadyExistException exception)
    {
        String msg = exception.getMessage();
        List<String> details = new ArrayList<>();
        details.add("Id Already Exists");
        HttpStatus status = HttpStatus.CONFLICT;
        ApiErrors errors = new ApiErrors(msg,details,status, LocalDateTime.now());
        return ResponseEntity.status(status).body(errors);
    }
    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<Object> handleTypeMismatchException(TypeMismatchException exception)
    {
        String msg = exception.getMessage();
        List<String> details = new ArrayList<>();
        details.add("Give Positive Value");
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiErrors errors = new ApiErrors(msg,details,status,LocalDateTime.now());
        return ResponseEntity.status(status).body(errors);
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException exception)
    {
        String msg=exception.getMessage();
        List<String> details=new ArrayList<>();
        details.add("NO records found");
        HttpStatus status=HttpStatus.NOT_FOUND;
        ApiErrors errors=new ApiErrors(msg, details, status, LocalDateTime.now());
        return ResponseEntity.status(status).body(errors);
    }
    @ExceptionHandler(FieldEmptyException.class)
    public ResponseEntity<Object> handleFieldEmptyException(FieldEmptyException exception)
    {
        String msg=exception.getMessage();
        List<String> details=new ArrayList<>();
        details.add("Empty fields not allowed");
        HttpStatus status=HttpStatus.NOT_ACCEPTABLE;
        ApiErrors errors=new ApiErrors(msg, details, status, LocalDateTime.now());
        return ResponseEntity.status(status).body(errors);
    }
}

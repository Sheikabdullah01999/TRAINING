package com.grootan.assetManagement.exception;

import com.grootan.assetManagement.response.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler
{

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException exception)
    {
        return   new ResponseEntity<>(
                new Response<>(String.valueOf(HttpStatus.NOT_FOUND.value()),
                        HttpStatus.NOT_FOUND, exception.getMessage()),
                new HttpHeaders(), HttpStatus.OK);
    }
    @ExceptionHandler(FieldEmptyException.class)
    public ResponseEntity<Object> handleFieldEmptyException(FieldEmptyException exception)
    {
        return new ResponseEntity<>(new Response<>(String.valueOf(HttpStatus.NOT_ACCEPTABLE),
                HttpStatus.NOT_ACCEPTABLE,exception.getMessage()),
                new HttpHeaders(),HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<Object> handleAlreadyExistsException(AlreadyExistsException exception)
    {
        return new ResponseEntity<>(new Response<>(String.valueOf(HttpStatus.CONFLICT),
                HttpStatus.CONFLICT,exception.getMessage()),
                new HttpHeaders(),HttpStatus.CONFLICT);
    }

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<Object> handleGeneralException(GeneralException exception)
    {
        return new ResponseEntity<>(new Response<>(String.valueOf(HttpStatus.NOT_ACCEPTABLE),
                HttpStatus.NOT_ACCEPTABLE,exception.getMessage()),
                new HttpHeaders(),HttpStatus.NOT_ACCEPTABLE);
    }

}
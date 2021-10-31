package com.shoppingmall.api.exceptions;

import com.shoppingmall.exceptions.NoOrderExistException;
import com.shoppingmall.exceptions.NoSuchMemberException;
import com.shoppingmall.exceptions.WrongDeliveryStatusException;
import com.shoppingmall.exceptions.WrongStatusException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.time.LocalDateTime;

@RestControllerAdvice("com.shoppingmall.api")
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NoOrderExistException.class)
    public ResponseEntity<ExceptionResponse> handleNoOrderExistException(Exception ex, WebRequest request){
        ExceptionResponse er = new ExceptionResponse(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity(er, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WrongDeliveryStatusException.class)
    public ResponseEntity<ExceptionResponse> handleWrongDeliveryStatusException(Exception ex, WebRequest request){
        ExceptionResponse er = new ExceptionResponse(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity(er, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ExceptionResponse> handleIOException(Exception ex, WebRequest request){
        ExceptionResponse er = new ExceptionResponse(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity(er, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WrongStatusException.class)
    public ResponseEntity<ExceptionResponse> handleWrongStatusException(Exception ex, WebRequest request){
        ExceptionResponse er = new ExceptionResponse(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity(er, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchMemberException.class)
    public ResponseEntity<ExceptionResponse> handleNoSuchMemberException(Exception ex, WebRequest request){
        ExceptionResponse er = new ExceptionResponse(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity(er, HttpStatus.NOT_FOUND);
    }

    /**
     * validation 실패시
     * @param ex
     * @param headers
     * @param status
     * @param request
     * @return
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        ExceptionResponse er = new ExceptionResponse(LocalDateTime.now(), ex.getBindingResult().getFieldError().getDefaultMessage(), ex.getBindingResult().toString());
        return new ResponseEntity(er, HttpStatus.BAD_REQUEST);
    }

}

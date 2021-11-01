package com.shoppingmall.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class WrongStatusException extends RuntimeException {

    public WrongStatusException(String s) {
        super(s);
    }

    public WrongStatusException(Throwable cause) {
        super(cause);
    }

    public WrongStatusException(String message, Throwable cause) {
        super(message, cause);
    }
}

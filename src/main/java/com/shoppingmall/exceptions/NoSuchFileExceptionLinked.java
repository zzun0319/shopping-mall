package com.shoppingmall.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoSuchFileExceptionLinked extends RuntimeException {

    public NoSuchFileExceptionLinked(String message) {
        super(message);
    }

    public NoSuchFileExceptionLinked(Throwable cause) {
        super(cause);
    }
}

package com.shoppingmall.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoSuchItemException extends RuntimeException {

    public NoSuchItemException(String s) {
        super(s);
    }

}

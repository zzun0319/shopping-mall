package com.shoppingmall.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoSuchMemberException extends RuntimeException {

    public NoSuchMemberException() {
        super();
    }

    public NoSuchMemberException(String s) {
        super(s);
    }

}

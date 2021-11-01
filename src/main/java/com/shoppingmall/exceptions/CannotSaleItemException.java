package com.shoppingmall.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CannotSaleItemException extends RuntimeException {

    public CannotSaleItemException(String s) {
        super(s);
    }

    public CannotSaleItemException(Throwable cause) {
        super(cause);
    }
}

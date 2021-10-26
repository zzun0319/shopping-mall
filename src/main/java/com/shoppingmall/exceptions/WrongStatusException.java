package com.shoppingmall.exceptions;

public class WrongStatusException extends IllegalArgumentException {

    public WrongStatusException() {
        super();
    }

    public WrongStatusException(String s) {
        super(s);
    }

    public WrongStatusException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongStatusException(Throwable cause) {
        super(cause);
    }
}

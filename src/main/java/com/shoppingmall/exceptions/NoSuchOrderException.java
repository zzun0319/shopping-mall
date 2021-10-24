package com.shoppingmall.exceptions;

public class NoSuchOrderException extends IllegalStateException{

    public NoSuchOrderException() {
        super();
    }

    public NoSuchOrderException(String s) {
        super(s);
    }

    public NoSuchOrderException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchOrderException(Throwable cause) {
        super(cause);
    }
}

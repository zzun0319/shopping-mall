package com.shoppingmall.exceptions;

public class NoSuchItemException extends IllegalStateException {

    public NoSuchItemException() {
        super();
    }

    public NoSuchItemException(String s) {
        super(s);
    }

    public NoSuchItemException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchItemException(Throwable cause) {
        super(cause);
    }
}

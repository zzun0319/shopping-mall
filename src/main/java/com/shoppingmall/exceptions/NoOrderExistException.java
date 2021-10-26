package com.shoppingmall.exceptions;

public class NoOrderExistException extends IllegalStateException {

    public NoOrderExistException() {
        super();
    }

    public NoOrderExistException(String s) {
        super(s);
    }

    public NoOrderExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoOrderExistException(Throwable cause) {
        super(cause);
    }
}

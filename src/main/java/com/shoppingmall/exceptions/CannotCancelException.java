package com.shoppingmall.exceptions;

public class CannotCancelException extends IllegalStateException{

    public CannotCancelException() {
        super();
    }

    public CannotCancelException(String s) {
        super(s);
    }

    public CannotCancelException(String message, Throwable cause) {
        super(message, cause);
    }

    public CannotCancelException(Throwable cause) {
        super(cause);
    }
}

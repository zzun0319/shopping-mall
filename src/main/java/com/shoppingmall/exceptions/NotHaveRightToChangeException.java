package com.shoppingmall.exceptions;

public class NotHaveRightToChangeException extends IllegalStateException {

    public NotHaveRightToChangeException() {
        super();
    }

    public NotHaveRightToChangeException(String s) {
        super(s);
    }

    public NotHaveRightToChangeException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotHaveRightToChangeException(Throwable cause) {
        super(cause);
    }
}

package com.shoppingmall.exceptions;

public class CannotChangeAddressException extends IllegalStateException {

    public CannotChangeAddressException() {
        super();
    }

    public CannotChangeAddressException(String s) {
        super(s);
    }

    public CannotChangeAddressException(String message, Throwable cause) {
        super(message, cause);
    }

    public CannotChangeAddressException(Throwable cause) {
        super(cause);
    }
}

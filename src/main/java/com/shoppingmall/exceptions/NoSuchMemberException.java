package com.shoppingmall.exceptions;

public class NoSuchMemberException extends IllegalStateException {

    public NoSuchMemberException() {
        super();
    }

    public NoSuchMemberException(String s) {
        super(s);
    }

    public NoSuchMemberException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchMemberException(Throwable cause) {
        super(cause);
    }
}

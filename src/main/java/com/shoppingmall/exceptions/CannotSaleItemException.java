package com.shoppingmall.exceptions;

public class CannotSaleItemException extends IllegalStateException {

    public CannotSaleItemException() {
        super();
    }

    public CannotSaleItemException(String s) {
        super(s);
    }

    public CannotSaleItemException(String message, Throwable cause) {
        super(message, cause);
    }

    public CannotSaleItemException(Throwable cause) {
        super(cause);
    }
}

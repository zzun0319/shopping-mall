package com.shoppingmall.exceptions;

public class NotEnoughStockException extends IllegalStateException {

    public NotEnoughStockException() {
        super();
    }

    public NotEnoughStockException(String s) {
        super(s);
    }

    public NotEnoughStockException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotEnoughStockException(Throwable cause) {
        super(cause);
    }
}

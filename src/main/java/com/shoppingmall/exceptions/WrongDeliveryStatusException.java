package com.shoppingmall.exceptions;

public class WrongDeliveryStatusException extends IllegalStateException {

    public WrongDeliveryStatusException() {
        super();
    }

    public WrongDeliveryStatusException(String s) {
        super(s);
    }

    public WrongDeliveryStatusException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongDeliveryStatusException(Throwable cause) {
        super(cause);
    }
}

package com.shoppingmall.enums;

public enum PaymentStatus {

    COMPLETE("결제 완료"), NOT_YET("결제 미완료");

    private final String desc;

    PaymentStatus(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}

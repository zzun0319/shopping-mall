package com.shoppingmall.domain.enums;

public enum OrderStatus {
    ORDER("주문 완료"), CANCEL("주문 취소");

    private final String desc;

    OrderStatus(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}

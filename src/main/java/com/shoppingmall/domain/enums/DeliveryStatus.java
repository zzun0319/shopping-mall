package com.shoppingmall.domain.enums;

public enum DeliveryStatus {

    COMPLETE("배송완료"), ING("배송중"), BEFORE("배송대기"), CANCEL("배송취소");

    private final String description;

    DeliveryStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

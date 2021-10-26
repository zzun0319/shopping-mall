package com.shoppingmall.enums;

public enum Grade {
    USER("일반회원"), VIP("VIP"), ADMIN("관리자");

    private String desc;

    Grade(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}

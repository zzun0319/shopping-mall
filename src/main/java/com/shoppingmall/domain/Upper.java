package com.shoppingmall.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Getter
@DiscriminatorValue("U")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Upper extends Item {

    private int armLength;
    private int totalLength;
    private int shoulderWidth;

    private Upper(String name, Integer price, Integer stockQuantity, Member salesman, int armLength, int totalLength, int shoulderWidth) {
        super(name, price, stockQuantity, salesman);
        this.armLength = armLength;
        this.totalLength = totalLength;
        this.shoulderWidth = shoulderWidth;
    }

    public static Upper createUpper(String name, Integer price, Integer stockQuantity, Member salesman, int armLength, int totalLength, int shoulderWidth){
        return new Upper(name, price, stockQuantity, salesman, armLength, totalLength, shoulderWidth);
    }
}

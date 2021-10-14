package com.shoppingmall.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Getter
@DiscriminatorValue("O")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Outer extends Item {

    private int totalLength;
    private int weight;
    private int armLength;

    private Outer(String name, Integer price, Integer stockQuantity, Member salesman, int totalLength, int weight, int armLength) {
        super(name, price, stockQuantity, salesman);
        this.totalLength = totalLength;
        this.weight = weight;
        this.armLength = armLength;
    }

    public static Outer createOuter(String name, Integer price, Integer stockQuantity, Member salesman, int totalLength, int weight, int armLength) {
        return new Outer(name, price, stockQuantity, salesman, totalLength, weight, armLength);
    }
}

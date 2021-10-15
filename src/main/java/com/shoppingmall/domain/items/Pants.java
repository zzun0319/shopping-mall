package com.shoppingmall.domain.items;

import com.shoppingmall.domain.members.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Getter
@DiscriminatorValue("P")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Pants extends Item {

    private int totalLength;
    private int waist;
    private int thighWidth;

    private Pants(String name, Integer price, Integer stockQuantity, Member salesman, int totalLength, int waist, int thighWidth) {
        super(name, price, stockQuantity, salesman);
        this.totalLength = totalLength;
        this.waist = waist;
        this.thighWidth = thighWidth;
    }

    public static Pants createPants(String name, Integer price, Integer stockQuantity, Member salesman, int totalLength, int waist, int thighWidth) {
        return new Pants(name, price, stockQuantity, salesman, totalLength, waist, thighWidth);
    }
}

package com.shoppingmall.valuetype;

import lombok.*;

import javax.persistence.Embeddable;

@Embeddable
@Getter @ToString
@AllArgsConstructor
@NoArgsConstructor
public class Address {

    private String city;
    private String street;
    private String zipcode;

    public String getFullAddress() {
        return String.format("{} {} {}", city, street, zipcode);
    }
}

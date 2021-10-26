package com.shoppingmall.domain.orders.forms;

import com.shoppingmall.enums.PaymentOption;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class OrderForm {

    private Long memberId;
    private Long itemId;
    private int orderQuantity;
    private String city;
    private String street;
    private String zipcode;
    private PaymentOption option;
}

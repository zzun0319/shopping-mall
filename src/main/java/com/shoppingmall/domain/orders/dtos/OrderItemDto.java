package com.shoppingmall.domain.orders.dtos;

import com.shoppingmall.domain.orders.OrderItem;
import lombok.Data;

@Data
public class OrderItemDto {

    private Long id;
    private String itemName;
    private Integer itemUnitPrice;
    private Integer orderQuantity;
    private Integer totalPrice;

    public OrderItemDto(OrderItem orderItem){
        id = orderItem.getId();
        itemName = orderItem.getItem().getName();
        itemUnitPrice = orderItem.getItem().getPrice();
        orderQuantity = orderItem.getOrderQuantity();
        totalPrice = orderItem.getTotalPrice();
    }
}

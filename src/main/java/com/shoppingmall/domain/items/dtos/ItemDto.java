package com.shoppingmall.domain.items.dtos;

import com.shoppingmall.domain.items.Item;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter @Getter
@ToString
public class ItemDto {

    private Long id;
    private String name;
    private Integer price;
    private Integer stockQuantity;

    public ItemDto(Item item) {
        id = item.getId();
        name = item.getName();
        price = item.getPrice();
        stockQuantity = item.getStockQuantity();
    }
}

package com.shoppingmall.domain.items.forms;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ItemDetailForm {

    private Long itemId;
    private String itemName;
    private String itemPrice;
    private int stockQuantity;
    private String salesmanName;
}

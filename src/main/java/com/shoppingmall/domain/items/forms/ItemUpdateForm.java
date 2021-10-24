package com.shoppingmall.domain.items.forms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemUpdateForm {

    private String itemName;
    private Integer price;
    private Integer quantity;

}

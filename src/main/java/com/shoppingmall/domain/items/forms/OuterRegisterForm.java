package com.shoppingmall.domain.items.forms;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OuterRegisterForm extends ItemRegisterForm {
    private int totalLength;
    private int weight;
    private int armLength;
}

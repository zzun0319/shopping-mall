package com.shoppingmall.domain.items.forms;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class UpperRegisterForm extends ItemRegisterForm {

    @NotEmpty
    private int armLength;

    @NotEmpty
    private int totalLength;

    @NotEmpty
    private int shoulderWidth;

}

package com.shoppingmall.domain.items.forms;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PantsRegisterForm extends ItemRegisterForm {

    private int totalLength;
    private int waist;
    private int thighWidth;
}

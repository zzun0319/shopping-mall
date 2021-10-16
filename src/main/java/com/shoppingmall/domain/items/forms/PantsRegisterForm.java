package com.shoppingmall.domain.items.forms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
@NoArgsConstructor
public class PantsRegisterForm extends ItemRegisterForm {

    private int totalLength;
    private int waist;
    private int thighWidth;

    public PantsRegisterForm(String name, Integer price, Integer stockQuantity,Long salesmanId, String dType, int totalLength, int waist, int thighWidth) {
        super(name, price, stockQuantity, salesmanId, dType);
        this.totalLength = totalLength;
        this.waist = waist;
        this.thighWidth = thighWidth;
    }
}

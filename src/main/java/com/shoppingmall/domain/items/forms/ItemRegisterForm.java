package com.shoppingmall.domain.items.forms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRegisterForm {

    @NotEmpty
    protected String name;

    @NotEmpty
    @Range(min = 1000, max = 1000000)
    protected Integer price;

    @NotEmpty
    @Range(min = 1, max = 9999)
    protected Integer stockQuantity;

    @NotEmpty
    protected Long salesmanId;

    @NotEmpty
    protected String dType;
}

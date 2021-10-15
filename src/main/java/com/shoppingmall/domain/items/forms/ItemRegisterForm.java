package com.shoppingmall.domain.items.forms;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotEmpty;

@MappedSuperclass
@Getter @Setter
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

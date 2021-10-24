package com.shoppingmall.domain.orders.forms;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
public class AddressForm {

    @NotEmpty(message = "시, 도를 입력해주세요.")
    private String city;

    @NotEmpty(message = "도로명 주소를 입력해주세요.")
    private String street;

    @NotEmpty(message = "우편번호를 입력해주세요.")
    private String zipcode;
}

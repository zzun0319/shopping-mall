package com.shoppingmall.domain.payment.forms;

import com.shoppingmall.enums.PaymentOption;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class PayForm {

    @NotNull(message = "결제수단을 선택하세요")
    private PaymentOption option;

    @NotNull(message = "결제할 금액을 입력하세요")
    private Integer paidPrice;

    private Integer totalPrice;
}

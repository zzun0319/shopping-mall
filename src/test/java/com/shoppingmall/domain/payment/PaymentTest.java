package com.shoppingmall.domain.payment;

import com.shoppingmall.domain.enums.PaymentOption;
import com.shoppingmall.domain.enums.PaymentStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    @Test
    @DisplayName("결제를 완료하면, 상태도 바뀐다.")
    void pay() {

        Payment payment = new Payment();
        payment.pay(10000, PaymentOption.CREDIT_CARD);

        Assertions.assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETE);
    }
}
package com.shoppingmall.converter;

import com.shoppingmall.domain.enums.PaymentOption;
import org.springframework.core.convert.converter.Converter;

public class StringToPaymentOptionConverter implements Converter<String, PaymentOption> {
    @Override
    public PaymentOption convert(String option) {
        return PaymentOption.valueOf(option);
    }
}

package com.shoppingmall.converter;

import com.shoppingmall.domain.enums.PaymentOption;
import org.springframework.core.convert.converter.Converter;

public class PaymentOptionToStringConverter implements Converter<PaymentOption, String> {
    @Override
    public String convert(PaymentOption option) {
        return String.valueOf(option);
    }
}

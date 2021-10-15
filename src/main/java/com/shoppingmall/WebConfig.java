package com.shoppingmall;

import com.shoppingmall.converter.PaymentOptionToStringConverter;
import com.shoppingmall.converter.StringToPaymentOptionConverter;
import com.shoppingmall.formatter.NumberFormatter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new PaymentOptionToStringConverter());
        registry.addConverter(new StringToPaymentOptionConverter());
        registry.addFormatter(new NumberFormatter());
    }
}

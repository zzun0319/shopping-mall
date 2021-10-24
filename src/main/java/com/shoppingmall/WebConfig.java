package com.shoppingmall;

import com.shoppingmall.converter.PaymentOptionToStringConverter;
import com.shoppingmall.converter.StringToPaymentOptionConverter;
import com.shoppingmall.formatter.NumberFormatter;
import com.shoppingmall.interceptors.AdminCheckInterceptor;
import com.shoppingmall.interceptors.LogInterceptor;
import com.shoppingmall.interceptors.LoginCheckInterceptor;
import com.shoppingmall.interceptors.SalesAvailableCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new PaymentOptionToStringConverter());
        registry.addConverter(new StringToPaymentOptionConverter());
        registry.addFormatter(new NumberFormatter());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/*.ico", "/error");

        registry.addInterceptor(new LoginCheckInterceptor())
                .order(2)
                .addPathPatterns("/**")
                .excludePathPatterns("/", "/members", "/members/login", "/members/logout", "/css/**", "/*.ico", "/error");

        registry.addInterceptor(new SalesAvailableCheckInterceptor())
                .order(3)
                .addPathPatterns("/items/type", "/items/add", "/items/edit/**", "/items/sales/**")
                .excludePathPatterns("/css/**", "/*.ico", "/error");

        registry.addInterceptor(new AdminCheckInterceptor())
                .order(4)
                .addPathPatterns("/members/permit/**")
                .excludePathPatterns("/css/**", "/*.ico", "/error");
    }
}

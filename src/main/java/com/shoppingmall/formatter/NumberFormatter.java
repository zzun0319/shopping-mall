package com.shoppingmall.formatter;

import org.springframework.format.Formatter;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class NumberFormatter implements Formatter<Number> {


    @Override // "1,000" -> 1000
    public Number parse(String text, Locale locale) throws ParseException {
        NumberFormat format = NumberFormat.getInstance(locale);
        Number number = format.parse(text);
        return number;
    }

    @Override // 1000 -> "1,000"
    public String print(Number object, Locale locale) {
        NumberFormat printer = NumberFormat.getInstance(locale);
        String string = printer.format(object);
        return string;
    }
}

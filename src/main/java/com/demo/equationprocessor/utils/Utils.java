package com.demo.equationprocessor.utils;

import java.util.regex.Pattern;

public class Utils {

    public static boolean isDouble(String value) {
        return value.matches("-?\\d+(.\\d+)?");
    }


    public static boolean isVariable(String value) {
        String decimalPattern = "-?[a-zA-Z]";
        return Pattern.matches(decimalPattern, value) && (value.length() == 1 || value.length() == 2);
    }


    public static boolean isOperationSymbol(Character character) {
        return !Character.isDigit(character) && !Character.isLetter(character);
    }
}

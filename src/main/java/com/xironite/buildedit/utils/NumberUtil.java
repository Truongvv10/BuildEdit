package com.xironite.buildedit.utils;

public class NumberUtil {
    public static boolean isInteger(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isDouble(String string) {
        try {
            Double.parseDouble(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isFloat(String string) {
        try {
            Float.parseFloat(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String toFormattedNumber(int number) {
        return String.format("%,d", number);
    }

    public static String toFormattedNumber(long number) {
        return String.format("%,d", number);
    }
}

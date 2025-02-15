package io.github.tavstal.portallock.utils;

/**
 * Utility class for determining the numeric type of string.
 */
public class MathUtils {

    /**
     * Checks if the provided string can be parsed as a byte.
     *
     * @param str the string to check
     * @return {@code true} if the string can be parsed as a byte; {@code false} otherwise
     */
    public static boolean isByte(String str) {
        try {
            Byte.parseByte(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Checks if the provided string can be parsed as an integer.
     *
     * @param str the string to check
     * @return {@code true} if the string can be parsed as an integer; {@code false} otherwise
     */
    public static boolean isInt(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Checks if the provided string can be parsed as a float.
     *
     * @param str the string to check
     * @return {@code true} if the string can be parsed as a float; {@code false} otherwise
     */
    public static boolean isFloat(String str) {
        try {
            Float.parseFloat(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Checks if the provided string can be parsed as a double (decimal).
     *
     * @param str the string to check
     * @return {@code true} if the string can be parsed as a double; {@code false} otherwise
     */
    public static boolean isDecimal(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}


//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.yourmq.utils;

public class StringUtil {
    public StringUtil() {
    }

    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

    public static boolean isInteger(String str) {
        return isNumberDo(str, false);
    }

    public static boolean isNumber(String str) {
        return isNumberDo(str, true);
    }

    private static boolean isNumberDo(String str, boolean incDot) {
        if (str != null && str.length() != 0) {
            int l = str.length();
            int start = str.charAt(0) != '-' && str.charAt(0) != '+' ? 0 : 1;
            boolean hasDot = false;

            for (int i = start; i < l; ++i) {
                int ch = str.charAt(i);
                if (incDot && ch == '.') {
                    if (hasDot) {
                        return false;
                    }

                    hasDot = true;
                } else if (!Character.isDigit(ch)) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }
}

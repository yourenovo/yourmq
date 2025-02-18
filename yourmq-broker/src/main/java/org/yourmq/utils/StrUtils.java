package org.yourmq.utils;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class StrUtils {
    public StrUtils() {
    }

    public static String guid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

    public static boolean isEmpty(Collection s) {
        return s == null || s.size() == 0;
    }

    public static boolean isEmpty(Map s) {
        return s == null || s.size() == 0;
    }

    public static boolean isNotEmpty(String s) {
        return !isEmpty(s);
    }

    public static boolean isNotEmpty(Collection s) {
        return !isEmpty(s);
    }
}

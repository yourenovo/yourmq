
package org.yourmq.base;

public class SessionUtils {
    public SessionUtils() {
    }

    public static boolean isActive(ClientSession s) {
        return s != null && s.isActive();
    }

    public static boolean isValid(ClientSession s) {
        return s != null && s.isValid();
    }
}

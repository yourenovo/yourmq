//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.yourmq.utils;

import org.yourmq.exception.YourSocketException;

import java.io.Reader;
import java.sql.Clob;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BeanUtil {
    private static final Map<String, Class<?>> clzCached = new ConcurrentHashMap();

    public BeanUtil() {
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static Class<?> loadClass(String clzName) {
        if (StringUtil.isEmpty(clzName)) {
            return null;
        } else {
            try {
                Class<?> clz = (Class) clzCached.get(clzName);
                if (clz == null) {
                    clz = Class.forName(clzName);
                    clzCached.put(clzName, clz);
                }

                return clz;
            } catch (RuntimeException var2) {
                throw var2;
            } catch (Throwable var3) {
                throw new YourSocketException("Failed to load class: " + clzName, var3);
            }
        }
    }

    public static String clobToString(Clob clob) {
        Reader reader = null;
        StringBuilder buf = new StringBuilder();

        try {
            reader = clob.getCharacterStream();
            char[] chars = new char[2048];

            while (true) {
                int len = reader.read(chars, 0, chars.length);
                if (len < 0) {
                    break;
                }

                buf.append(chars, 0, len);
            }
        } catch (Throwable var6) {
            throw new YourSocketException("Read string from reader error", var6);
        }

        String text = buf.toString();
        if (reader != null) {
            try {
                reader.close();
            } catch (Throwable var5) {
                throw new YourSocketException("Read string from reader error", var5);
            }
        }

        return text;
    }

    public static Object newInstance(Class<?> clz) {
        try {
            return clz.isInterface() ? null : clz.getDeclaredConstructor().newInstance();
        } catch (Throwable var2) {
            throw new YourSocketException("The instantiation failed, class: " + clz.getName(), var2);
        }
    }
}

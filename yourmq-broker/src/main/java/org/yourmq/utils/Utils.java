//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.yourmq.utils;

import io.micrometer.common.lang.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.*;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

public class Utils {
    private static ReentrantLock comLocker = new ReentrantLock();
    private static final FileNameMap mimeMap = URLConnection.getFileNameMap();
    private static final char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static AtomicReference<String> _appFolder;
    private static String _pid;

    public Utils() {
    }

    public static ReentrantLock locker() {
        return comLocker;
    }

    public static Future<?> async(Runnable task) {
        return RunUtils.async(task);
    }

    public static boolean ping(String address) throws Exception {
        if (address.contains(":")) {
            String host = address.split(":")[0];
            int port = Integer.parseInt(address.split(":")[1]);

            try {
                Socket socket = new Socket();
                Throwable var4 = null;

                boolean var6;
                try {
                    SocketAddress addr = new InetSocketAddress(host, port);
                    socket.connect(addr, 3000);
                    var6 = true;
                } catch (Throwable var16) {
                    var4 = var16;
                    throw var16;
                } finally {
                    if (socket != null) {
                        if (var4 != null) {
                            try {
                                socket.close();
                            } catch (Throwable var15) {
                                var4.addSuppressed(var15);
                            }
                        } else {
                            socket.close();
                        }
                    }

                }

                return var6;
            } catch (IOException var18) {
                return false;
            }
        } else {
            return InetAddress.getByName(address).isReachable(3000);
        }
    }

    public static <T> List<T> asList(T[] ary) {
        if (ary == null) {
            return null;
        } else {
            List<T> list = new ArrayList(ary.length);
            Collections.addAll(list, ary);
            return list;
        }
    }

    public static String mime(String fileName) {
        String tmp = mimeMap.getContentTypeFor(fileName);
        return tmp == null ? "application/octet-stream" : tmp;
    }

    public static String annoAlias(String v1, String v2) {
        return isEmpty(v1) ? v2 : v1;
    }

    public static String valueOr(String... optionalValues) {
        String[] var1 = optionalValues;
        int var2 = optionalValues.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            String v = var1[var3];
            if (isNotEmpty(v)) {
                return v;
            }
        }

        return null;
    }

    public static String propertyOr(Properties props, String... optionalNames) {
        String[] var2 = optionalNames;
        int var3 = optionalNames.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            String n = var2[var4];
            String v = props.getProperty(n);
            if (isNotEmpty(v)) {
                return v;
            }
        }

        return null;
    }

    public static void propertyRemove(Properties props, String... optionalNames) {
        String[] var2 = optionalNames;
        int var3 = optionalNames.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            String n = var2[var4];
            props.remove(n);
        }

    }

    public static boolean isProxyClass(Class<?> clz) {
        return clz.getName().contains("$$Solon");
    }

    public static String guid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String md5(String str) {
        try {
            byte[] btInput = str.getBytes("UTF-8");
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char[] chars = new char[j * 2];
            int k = 0;

            for (int i = 0; i < j; ++i) {
                byte byte0 = md[i];
                chars[k++] = HEX_DIGITS[byte0 >>> 4 & 15];
                chars[k++] = HEX_DIGITS[byte0 & 15];
            }

            return new String(chars);
        } catch (Exception var9) {
            throw new RuntimeException(var9);
        }
    }

    public static String throwableToString(Throwable ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    public static Throwable throwableUnwrap(Throwable ex) {
        Throwable th = ex;

        while (true) {
            while (true) {
                while (th instanceof InvocationTargetException) {
                    th = ((InvocationTargetException) th).getTargetException();
                }

                if (!(th instanceof UndeclaredThrowableException)) {
                    if (th.getClass() != RuntimeException.class || th.getCause() == null) {
                        return th;
                    }

                    th = th.getCause();
                } else {
                    th = ((UndeclaredThrowableException) th).getUndeclaredThrowable();
                }
            }
        }
    }

    public static boolean throwableHas(Throwable ex, Class<? extends Throwable> clz) {
        Throwable th = ex;

        while (!clz.isAssignableFrom(th.getClass())) {
            if (th instanceof InvocationTargetException) {
                th = ((InvocationTargetException) th).getTargetException();
            } else if (th instanceof UndeclaredThrowableException) {
                th = ((UndeclaredThrowableException) th).getUndeclaredThrowable();
            } else {
                if (th.getCause() == null) {
                    return false;
                }

                th = th.getCause();
            }
        }

        return true;
    }

    public static String trimDuplicates(String str, char c) {
        int start = 0;

        while ((start = str.indexOf(c, start) + 1) > 0) {
            int end;
            for (end = start; end < str.length() && str.charAt(end) == c; ++end) {
            }

            if (end > start) {
                str = str.substring(0, start) + str.substring(end);
            }
        }

        return str;
    }

    public static String snakeToCamel(String name) {
        if (name.indexOf(45) < 0) {
            return name;
        } else {
            String[] ss = name.split("-");
            StringBuilder sb = new StringBuilder(name.length());
            sb.append(ss[0]);

            for (int i = 1; i < ss.length; ++i) {
                if (ss[i].length() > 1) {
                    sb.append(ss[i].substring(0, 1).toUpperCase()).append(ss[i].substring(1));
                } else {
                    sb.append(ss[i].toUpperCase());
                }
            }

            return sb.toString();
        }
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


    public static <T> boolean isEmpty(T[] s) {
        return s == null || s.length == 0;
    }

    public static boolean isNotEmpty(String s) {
        return !isEmpty(s);
    }

    public static boolean isNotEmpty(Collection s) {
        return !isEmpty(s);
    }

    public static boolean isNotEmpty(Map s) {
        return !isEmpty(s);
    }

    public static boolean isBlank(String s) {
        if (isEmpty(s)) {
            return true;
        } else {
            int i = 0;

            for (int l = s.length(); i < l; ++i) {
                if (!isWhitespace(s.codePointAt(i))) {
                    return false;
                }
            }

            return true;
        }
    }

    public static boolean isNotBlank(String s) {
        return !isBlank(s);
    }

    public static boolean isWhitespace(int c) {
        return c == 32 || c == 9 || c == 10 || c == 12 || c == 13;
    }

    public static <T> T firstOrNull(List<T> list) {
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

    public static <T> T[] toArray(List<T> list, T[] a) {
        return list != null ? list.toArray(a) : null;
    }

    public static Locale toLocale(String lang) {
        if (lang == null) {
            return null;
        } else {
            String[] ss = lang.split("_|-");
            if (ss.length >= 3) {
                return ss[1].length() > 2 ? new Locale(ss[0], ss[2], ss[1]) : new Locale(ss[0], ss[1], ss[2]);
            } else if (ss.length == 2) {
                return ss[1].length() > 2 ? new Locale(ss[0], "", ss[1]) : new Locale(ss[0], ss[1]);
            } else {
                return new Locale(ss[0]);
            }
        }
    }


    public static <T> T injectProperties(T obj, Properties propS) {
        return null;
    }

    public static String getFullStackTrace(Throwable ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw, true));
        return sw.getBuffer().toString();
    }

    @Nullable
    public static String appFolder() {
        return "";
    }

    public static File getFile(String uri) {
        if (uri == null) {
            return null;
        } else {
            String appDir = appFolder();
            File file = null;
            if (appDir != null) {
                if (uri.startsWith("./")) {
                    file = new File(appDir, uri.substring(2));
                } else if (!uri.contains("/")) {
                    file = new File(appDir, uri);
                }
            }

            if (file == null) {
                file = new File(uri);
            }

            return file;
        }
    }

    public static File getFolderAndMake(String uri, boolean autoMake) {
        File extDir = getFile(uri);
        if (extDir != null && autoMake && !extDir.exists()) {
            extDir.mkdirs();
        }

        return extDir;
    }

    public static void bindTo(Map<String, String> source, Object target) {
        bindTo((k) -> {
            return (String) source.get(k);
        }, target);
    }

    public static void bindTo(Properties source, Object target) {
        injectProperties(target, source);
    }

    public static void bindTo(Function<String, String> source, Object target) {
    }

    public static ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public static ClassLoader getClassLoader() {
        ClassLoader classLoader = getContextClassLoader();
        if (classLoader == null) {
            classLoader = Utils.class.getClassLoader();
            if (null == classLoader) {
                classLoader = ClassLoader.getSystemClassLoader();
            }
        }

        return classLoader;
    }

    public static String pid() {
        if (_pid == null) {
            RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
            _pid = rb.getName().split("@")[0];
            System.setProperty("PID", _pid);
        }

        return _pid;
    }
}

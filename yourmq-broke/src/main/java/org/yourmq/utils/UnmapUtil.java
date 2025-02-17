package org.yourmq.utils;

import com.sun.org.slf4j.internal.LoggerFactory;
import org.slf4j.Logger;

import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class UnmapUtil {
    private static Logger log = (Logger) LoggerFactory.getLogger(UnmapUtil.class);
    private static Method unmapMethod;

    public UnmapUtil() {
    }

    public static void unmap(FileChannel fileC, MappedByteBuffer buffer) {
        if (fileC != null && buffer != null) {
            try {
                if (unmapMethod == null) {
                    unmapMethod = fileC.getClass().getDeclaredMethod("unmap", MappedByteBuffer.class);
                    unmapMethod.setAccessible(true);
                }

                unmapMethod.invoke(unmapMethod.getDeclaringClass(), buffer);
            } catch (Exception var3) {
                log.warn("MappedByteBuffer unmap failure", var3);
            }

        }
    }
}
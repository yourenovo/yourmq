package org.yourmq.utils;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

public class MemoryUtils {
    private static MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

    public MemoryUtils() {
    }

    public static float getUseMemoryRatio() {
        MemoryUsage memoryUsage = memoryMXBean.getHeapMemoryUsage();
        return (float)memoryUsage.getUsed() * 1.0F / (float)memoryUsage.getMax();
    }
}
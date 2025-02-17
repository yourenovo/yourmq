//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.yourmq.utils;

import org.yourmq.client.base.NamedThreadFactory;
import org.yourmq.client.base.RunnableEx;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class RunUtils {
    private static ExecutorService singleExecutor = Executors.newSingleThreadExecutor((new NamedThreadFactory("Socketd-singleExecutor-")).daemon(true));
    private static ExecutorService asyncExecutor;
    private static ScheduledExecutorService scheduledExecutor;

    public RunUtils() {
    }

    public static void setScheduledExecutor(ScheduledExecutorService scheduledExecutor) {
        if (scheduledExecutor != null) {
            ScheduledExecutorService old = RunUtils.scheduledExecutor;
            RunUtils.scheduledExecutor = scheduledExecutor;
            old.shutdown();
        }

    }

    public static ScheduledExecutorService getScheduledExecutor() {
        return scheduledExecutor;
    }

    public static void setAsyncExecutor(ExecutorService asyncExecutor) {
        if (asyncExecutor != null) {
            ExecutorService old = RunUtils.asyncExecutor;
            RunUtils.asyncExecutor = asyncExecutor;
            old.shutdown();
        }

    }

    public static void runAndTry(RunnableEx task) {
        try {
            task.run();
        } catch (Throwable var2) {
        }

    }

    public static CompletableFuture<Void> single(Runnable task) {
        return CompletableFuture.runAsync(task, singleExecutor);
    }

    public static CompletableFuture<Void> async(Runnable task) {
        return CompletableFuture.runAsync(task, asyncExecutor);
    }

    public static <U> CompletableFuture<U> async(Supplier<U> task) {
        return CompletableFuture.supplyAsync(task, asyncExecutor);
    }

    public static CompletableFuture<Void> asyncAndTry(RunnableEx task) {
        return CompletableFuture.runAsync(() -> {
            runAndTry(task);
        }, asyncExecutor);
    }

    public static ScheduledFuture<?> delay(Runnable task, long millis) {
        return scheduledExecutor.schedule(task, millis, TimeUnit.MILLISECONDS);
    }

    public static ScheduledFuture<?> delayAndRepeat(Runnable task, long millis) {
        return scheduledExecutor.scheduleWithFixedDelay(task, 1000L, millis, TimeUnit.MILLISECONDS);
    }

    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long initialDelay, long millisPeriod) {
        return scheduledExecutor.scheduleAtFixedRate(task, initialDelay, millisPeriod, TimeUnit.MILLISECONDS);
    }

    public static ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long initialDelay, long millisDelay) {
        return scheduledExecutor.scheduleWithFixedDelay(task, initialDelay, millisDelay, TimeUnit.MILLISECONDS);
    }

    public static long milliSecondFromNano() {
        return System.nanoTime() / 1000000L;
    }

    static {
        int asyncPoolSize = Math.max(Runtime.getRuntime().availableProcessors(), 2);
        asyncExecutor = new ThreadPoolExecutor(asyncPoolSize, asyncPoolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue(), (new NamedThreadFactory("Socketd-asyncExecutor-")).daemon(true));
        int scheduledPoolSize = 2;
        scheduledExecutor = new ScheduledThreadPoolExecutor(scheduledPoolSize, (new NamedThreadFactory("Socketd-scheduledExecutor-")).daemon(true));
    }
}

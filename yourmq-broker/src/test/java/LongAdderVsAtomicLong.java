import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

public class LongAdderVsAtomicLong {

    private static final int INCREMENT_TIMES = 1000000;

    public static void main(String[] args) throws InterruptedException {
        // 并发量为 10 的测试
        test(5);
        // 并发量大于 1000 的测试
        test(100);
    }

    private static void test(int threadCount) throws InterruptedException {
        System.out.println("并发量: " + threadCount);

        // 测试 AtomicLong
        long atomicLongStartTime = System.currentTimeMillis();
        testAtomicLong(threadCount);
        long atomicLongEndTime = System.currentTimeMillis();
        System.out.println("AtomicLong 累加耗时: " + (atomicLongEndTime - atomicLongStartTime) + " 毫秒");

        // 测试 LongAdder
        long longAdderStartTime = System.currentTimeMillis();
        testLongAdder(threadCount);
        long longAdderEndTime = System.currentTimeMillis();
        System.out.println("LongAdder 累加耗时: " + (longAdderEndTime - longAdderStartTime) + " 毫秒");

        System.out.println();
    }

    private static void testAtomicLong(int threadCount) throws InterruptedException {
        AtomicLong atomicLong = new AtomicLong(0);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                for (int j = 0; j < INCREMENT_TIMES; j++) {
                    atomicLong.incrementAndGet();
                }
                latch.countDown();
            });
        }

        latch.await();
        executorService.shutdown();
    }

    private static void testLongAdder(int threadCount) throws InterruptedException {
        LongAdder longAdder = new LongAdder();
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                for (int j = 0; j < INCREMENT_TIMES; j++) {
                    longAdder.increment();
                }
                latch.countDown();
            });
        }

        latch.await();
        executorService.shutdown();
    }
}
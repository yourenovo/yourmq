package org.yourmq.inter;

import org.yourmq.base.IdGenerator;

import java.util.concurrent.atomic.AtomicLong;

public class TimeidGenerator implements IdGenerator {
    private AtomicLong basetime = new AtomicLong();
    private AtomicLong count = new AtomicLong();

    /**
     * 创建
     */
    @Override
    public String generate() {
        long tmp = System.currentTimeMillis() / 1000;
        if (tmp != basetime.get()) {
            basetime.set(tmp);
            count.set(0);
        }

        //1秒内可容1亿
        return String.valueOf(basetime.get() * 1000 * 1000 * 1000 + count.incrementAndGet());
    }
}
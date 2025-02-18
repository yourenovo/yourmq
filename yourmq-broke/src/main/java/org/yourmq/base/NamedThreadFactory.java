//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.yourmq.base;

import org.yourmq.utils.StrUtils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {
    private final String namePrefix;
    private final AtomicInteger threadCount = new AtomicInteger(0);
    private ThreadGroup group;
    private boolean daemon = false;
    private int priority = 5;

    public NamedThreadFactory(String namePrefix) {
        if (StrUtils.isEmpty(namePrefix)) {
            this.namePrefix = this.getClass().getSimpleName() + "-";
        } else {
            this.namePrefix = namePrefix;
        }

    }

    public NamedThreadFactory group(ThreadGroup group) {
        this.group = group;
        return this;
    }

    public NamedThreadFactory daemon(boolean daemon) {
        this.daemon = daemon;
        return this;
    }

    public NamedThreadFactory priority(int priority) {
        this.priority = priority;
        return this;
    }
@Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(this.group, r, this.namePrefix + this.threadCount.incrementAndGet());
        t.setDaemon(this.daemon);
        t.setPriority(this.priority);
        return t;
    }
}

package org.yourmq.snap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class YourmqStartupRunner implements CommandLineRunner {

    @Autowired
    private YourmqLifecycle yourmqLifecycle;

    @Override
    public void run(String... args) throws Exception {
        try {
            yourmqLifecycle.start();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
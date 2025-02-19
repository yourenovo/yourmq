package org.yourmq.inter;

import org.yourmq.base.IdGenerator;

import java.util.UUID;

public class GuidGenerator implements IdGenerator {
    public GuidGenerator() {
    }

    @Override
    public String generate() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}

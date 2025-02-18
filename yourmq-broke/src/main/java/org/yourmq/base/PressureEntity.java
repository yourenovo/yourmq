package org.yourmq.base;

import org.yourmq.common.StringEntity;

public class PressureEntity extends StringEntity {
    private static final PressureEntity instance = new PressureEntity();

    public static PressureEntity getInstance() {
        return instance;
    }

    public PressureEntity() {
        this("Too much pressure");
    }

    public PressureEntity(String description) {
        super(description);
        this.metaPut("code", String.valueOf(3001));
    }
}
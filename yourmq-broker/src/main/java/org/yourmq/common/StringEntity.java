package org.yourmq.common;

import java.nio.charset.StandardCharsets;

public class StringEntity extends EntityDefault {
    public StringEntity(String str) {
        this.dataSet(str.getBytes(StandardCharsets.UTF_8));
    }
}
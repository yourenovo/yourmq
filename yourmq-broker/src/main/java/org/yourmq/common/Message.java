package org.yourmq.common;

public interface Message extends Entity {
    default String atName() {
        return this.meta("@");
    }

    default int rangeStart() {
        return this.metaAsInt("Data-Range-Start");
    }

    default int rangeSize() {
        return this.metaAsInt("Data-Range-Size");
    }

    boolean isRequest();

    boolean isSubscribe();

    String sid();

    String event();

    Entity entity();
}

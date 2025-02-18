package org.yourmq.base;

import org.yourmq.common.Entity;

public interface Reply extends Entity {
    String sid();

    boolean isEnd();
}

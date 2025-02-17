package org.yourmq.client.base;

import org.yourmq.common.Message;

public interface MessageInternal extends Message, Reply {
    int flag();
}
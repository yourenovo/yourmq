package org.yourmq.base;

import org.yourmq.common.Entity;

import java.io.IOException;

public interface FragmentHandler {
    void spliFragment(Channel channel, StreamInternal stream, MessageInternal message, IoConsumer<Entity> consumer) throws IOException;

    Frame aggrFragment(Channel channel, int fragmentIndex, MessageInternal message) throws IOException;

    boolean aggrEnable();
}

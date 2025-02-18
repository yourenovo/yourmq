package org.yourmq.base;

import java.io.IOException;

@FunctionalInterface
public interface ClientConnectHandler {
    ChannelInternal clientConnect(ClientConnector connector) throws IOException;
}
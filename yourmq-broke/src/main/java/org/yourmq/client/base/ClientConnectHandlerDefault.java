package org.yourmq.client.base;

import java.io.IOException;

public class ClientConnectHandlerDefault implements ClientConnectHandler {
    public ClientConnectHandlerDefault() {
    }

    @Override
    public ChannelInternal clientConnect(ClientConnector connector) throws IOException {
        return connector.connect();
    }
}
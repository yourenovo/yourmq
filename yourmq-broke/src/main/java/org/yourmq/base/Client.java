package org.yourmq.base;

import java.io.IOException;

public interface Client {
    Client connectHandler(ClientConnectHandler connectHandler);

    Client heartbeatHandler(ClientHeartbeatHandler heartbeatHandler);

    Client config(ClientConfigHandler configHandler);

    Client listen(Listener listener);

    ClientSession open();

    ClientSession openOrThow() throws IOException;
}
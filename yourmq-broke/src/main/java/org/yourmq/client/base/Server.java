package org.yourmq.client.base;

import java.io.IOException;

public interface Server {
    String getTitle();

    ServerConfig getConfig();

    Server config(ServerConfigHandler configHandler);

    Server listen(Listener listener);

    Server start() throws IOException;

    void prestop();

    void stop();
}

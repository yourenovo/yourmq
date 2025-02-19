package org.yourmq.base;

import org.yourmq.inter.ServerConfig;

@FunctionalInterface
public interface ServerConfigHandler {
    void serverConfig(ServerConfig config);
}

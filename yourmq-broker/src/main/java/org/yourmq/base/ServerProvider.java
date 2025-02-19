package org.yourmq.base;

import org.yourmq.inter.ServerConfig;

public interface ServerProvider {
    String[] schemas();

    Server createServer(ServerConfig serverConfig);
}

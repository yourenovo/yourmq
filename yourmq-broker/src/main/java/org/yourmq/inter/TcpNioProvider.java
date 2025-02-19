package org.yourmq.inter;

import org.yourmq.base.ClientConfig;
import org.yourmq.base.*;


public class TcpNioProvider implements ClientProvider, ServerProvider {
    public TcpNioProvider() {
    }

    @Override
    public String[] schemas() {
        return new String[]{"tcp", "tcps", "tcp-netty", "sd:tcp", "sd:tcps", "sd:tcp-netty"};
    }

    @Override
    public Server createServer(ServerConfig serverConfig) {
        return new TcpNioServer(serverConfig);
    }

    @Override
    public Client createClient(ClientConfig clientConfig) {
        return new TcpNioClient(clientConfig);
    }
}

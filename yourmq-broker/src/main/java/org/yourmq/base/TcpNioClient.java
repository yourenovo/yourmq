package org.yourmq.base;


import io.netty.channel.Channel;
import org.yourmq.inter.TcpNioClientConnector;

public class TcpNioClient extends ClientBase<TcpNioChannelAssistant> implements ChannelSupporter<Channel> {
    public TcpNioClient(ClientConfig config) {
        super(config, new TcpNioChannelAssistant());
    }

    @Override
    protected ClientConnector createConnector() {
        return new TcpNioClientConnector(this);
    }
}

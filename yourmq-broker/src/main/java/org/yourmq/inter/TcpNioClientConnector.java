//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.yourmq.inter;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yourmq.base.ChannelInternal;
import org.yourmq.base.NamedThreadFactory;
import org.yourmq.base.TcpNioClient;
import org.yourmq.common.YourMQConnectionException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TcpNioClientConnector extends ClientConnectorBase<TcpNioClient> {
    private static final Logger log = LoggerFactory.getLogger(TcpNioClientConnector.class);
    private ChannelFuture real;
    private NioEventLoopGroup workGroup;

    public TcpNioClientConnector(TcpNioClient client) {
        super(client);
    }

    @Override
    public ChannelInternal connect() throws IOException {
        this.close();
        this.workGroup = new NioEventLoopGroup(this.getConfig().getCodecThreads(), new NamedThreadFactory("nettyTcpClientWork-"));

        try {
            Bootstrap bootstrap = new Bootstrap();
            NettyClientInboundHandler inboundHandler = new NettyClientInboundHandler(this.client);
            ChannelHandler handler = new NettyChannelInitializer(((TcpNioClient) this.client).getConfig(), inboundHandler);
            this.real = ((Bootstrap) ((Bootstrap) ((Bootstrap) ((Bootstrap) ((Bootstrap) bootstrap.group(this.workGroup)).option(ChannelOption.SO_RCVBUF, this.getConfig().getReadBufferSize())).option(ChannelOption.SO_SNDBUF, this.getConfig().getWriteBufferSize())).channel(NioSocketChannel.class)).handler(handler)).connect(((TcpNioClient) this.client).getConfig().getHost(), ((TcpNioClient) this.client).getConfig().getPort()).await();
            ClientHandshakeResult handshakeResult = (ClientHandshakeResult) inboundHandler.getHandshakeFuture().get(((TcpNioClient) this.client).getConfig().getConnectTimeout(), TimeUnit.MILLISECONDS);
            if (handshakeResult.getThrowable() != null) {
                throw handshakeResult.getThrowable();
            } else {
                return handshakeResult.getChannel();
            }
        } catch (TimeoutException var5) {
            this.close();
            throw new YourMQConnectionException("Connection timeout: " + ((TcpNioClient) this.client).getConfig().getLinkUrl());
        } catch (Throwable var6) {
            this.close();
            if (var6 instanceof IOException) {
                throw (IOException) var6;
            } else {
                throw new YourMQConnectionException("Connection failed: " + ((TcpNioClient) this.client).getConfig().getLinkUrl(), var6);
            }
        }
    }

    @Override
    public void close() {
        try {
            if (this.real != null) {
                this.real.channel().close();
            }

            if (this.workGroup != null) {
                this.workGroup.shutdownGracefully();
            }
        } catch (Throwable var2) {
            if (log.isDebugEnabled()) {
                log.debug("Client connector close error", var2);
            }
        }

    }
}

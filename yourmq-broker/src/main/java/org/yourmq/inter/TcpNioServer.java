/*
  Tcp-Nio 服务端实现（支持 ssl）
 */
package org.yourmq.inter;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yourmq.base.ChannelSupporter;
import org.yourmq.base.NamedThreadFactory;
import org.yourmq.base.Server;
import org.yourmq.base.TcpNioChannelAssistant;
import org.yourmq.common.YourSocket;
import org.yourmq.exception.YourSocketException;
import org.yourmq.utils.StrUtils;

import java.io.IOException;

public class TcpNioServer extends ServerBase<TcpNioChannelAssistant> implements ChannelSupporter<Channel> {
    private static final Logger log = LoggerFactory.getLogger(TcpNioServer.class);
    private ChannelFuture server;
    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workGroup;


    public TcpNioServer(ServerConfig config) {
        super(config, new TcpNioChannelAssistant());
    }

    @Override
    public String getTitle() {
        return "tcp/nio/netty 4.1/" + YourSocket.version();
    }

    @Override
    public Server start() throws IOException {
        if (this.isStarted) {
            throw new IllegalStateException("YourSocket server started");
        } else {
            this.isStarted = true;
            this.bossGroup = new NioEventLoopGroup(this.getConfig().getIoThreads(), new NamedThreadFactory("nettyTcpServerBoss-"));
            this.workGroup = new NioEventLoopGroup(this.getConfig().getCodecThreads(), new NamedThreadFactory("nettyTcpServerWork-"));

            try {
                NettyServerInboundHandler inboundHandler = new NettyServerInboundHandler(this);
                ChannelHandler channelHandler = new NettyChannelInitializer(this.getConfig(), inboundHandler);
                ServerBootstrap bootstrap = new ServerBootstrap();
                (bootstrap.group(this.bossGroup, this.workGroup).childOption(ChannelOption.SO_RCVBUF, this.getConfig().getReadBufferSize()).childOption(ChannelOption.SO_SNDBUF, this.getConfig().getWriteBufferSize()).channel(NioServerSocketChannel.class)).childHandler(channelHandler);
                if (StrUtils.isEmpty(this.getConfig().getHost())) {
                    this.server = bootstrap.bind(this.getConfig().getPort()).await();
                } else {
                    this.server = bootstrap.bind(this.getConfig().getHost(), this.getConfig().getPort()).await();
                }
            } catch (Exception var4) {
                this.bossGroup.shutdownGracefully();
                this.workGroup.shutdownGracefully();
                if (var4 instanceof IOException) {
                    throw (IOException) var4;
                }

                throw new YourSocketException("YourSocket server start failed!", var4);
            }

            log.info("YourSocket server started: {server=" + this.getConfig().getLocalUrl() + "}");
            return this;
        }
    }

    @Override
    public void stop() {
        if (this.isStarted) {
            this.isStarted = false;
            super.stop();

            try {
                if (this.server != null) {
                    this.server.channel().close();
                }

                if (this.bossGroup != null) {
                    this.bossGroup.shutdownGracefully();
                }

                if (this.workGroup != null) {
                    this.workGroup.shutdownGracefully();
                }
            } catch (Exception var2) {
                if (log.isDebugEnabled()) {
                    log.debug("Server stop error", var2);
                }
            }

        }
    }
}

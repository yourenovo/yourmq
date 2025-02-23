
package org.yourmq.inter;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.yourmq.base.Config;
import org.yourmq.base.Frame;

import javax.net.ssl.SSLEngine;
import java.util.concurrent.TimeUnit;

public class NettyChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final SimpleChannelInboundHandler<Frame> processor;
    private final Config config;

    public NettyChannelInitializer(Config config, SimpleChannelInboundHandler<Frame> processor) {
        this.processor = processor;
        this.config = config;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (this.config.getSslContext() != null) {
            SSLEngine engine = this.config.getSslContext().createSSLEngine();
            if (!this.config.clientMode()) {
                engine.setUseClientMode(false);
                engine.setNeedClientAuth(true);
            }

            pipeline.addFirst(new ChannelHandler[]{new SslHandler(engine)});
        }

        pipeline.addLast(new ChannelHandler[]{new LengthFieldBasedFrameDecoder(17825792, 0, 4, -4, 0)});
        pipeline.addLast(new ChannelHandler[]{new NettyMessageEncoder(this.config)});
        pipeline.addLast(new ChannelHandler[]{new NettyMessageDecoder(this.config)});
        if (this.config.getIdleTimeout() > 0L) {
            pipeline.addLast(new ChannelHandler[]{new IdleStateHandler(this.config.getIdleTimeout(), 0L, 0L, TimeUnit.MILLISECONDS)});
            pipeline.addLast(new ChannelHandler[]{new IdleTimeoutHandler(this.config)});
        }

        pipeline.addLast(new ChannelHandler[]{this.processor});
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.yourmq.inter;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.yourmq.base.ChannelDefault;
import org.yourmq.base.ChannelInternal;
import org.yourmq.base.Frame;

@Sharable
public class NettyServerInboundHandler extends SimpleChannelInboundHandler<Frame> {
    private static AttributeKey<ChannelInternal> CHANNEL_KEY = AttributeKey.valueOf("CHANNEL_KEY");
    private final TcpNioServer server;

    public NettyServerInboundHandler(TcpNioServer server) {
        this.server = server;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        ChannelInternal channel = new ChannelDefault(ctx.channel(), this.server);
        ctx.attr(CHANNEL_KEY).set(channel);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Frame frame) throws Exception {
        ChannelInternal channel = (ChannelInternal) ctx.attr(CHANNEL_KEY).get();
        this.server.getProcessor().reveFrame(channel, frame);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        ChannelInternal channel = (ChannelInternal) ctx.attr(CHANNEL_KEY).get();
        this.server.getProcessor().onClose(channel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ChannelInternal channel = (ChannelInternal) ctx.attr(CHANNEL_KEY).get();
        this.server.getProcessor().onError(channel, cause);
        ctx.close();
    }
}

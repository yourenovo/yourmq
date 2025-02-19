package org.yourmq.inter;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.yourmq.base.Config;
import org.yourmq.base.Frame;

public class NettyMessageEncoder extends MessageToByteEncoder<Frame> {
    private final Config config;

    public NettyMessageEncoder(Config config) {
        this.config = config;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Frame message, ByteBuf byteBuf) throws Exception {
        if (message != null) {
            NettyBufferCodecWriter writer = new NettyBufferCodecWriter(byteBuf);
            this.config.getCodec().write(message, (i) -> {
                return writer;
            });
        }

    }
}
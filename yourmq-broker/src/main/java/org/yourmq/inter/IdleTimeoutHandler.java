
package org.yourmq.inter;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yourmq.base.Config;

public class IdleTimeoutHandler extends ChannelDuplexHandler {
    private static final Logger log = LoggerFactory.getLogger(IdleTimeoutHandler.class);
    private final Config config;
    private final String role;

    public IdleTimeoutHandler(Config config) {
        this.config = config;
        this.role = config.clientMode() ? "Client" : "Server";
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) {
                if (log.isDebugEnabled()) {
                    log.debug("{} channel idle timeout, remoteIp={}", this.role, ctx.channel().remoteAddress());
                }

                ctx.close();
            }
        }

    }
}

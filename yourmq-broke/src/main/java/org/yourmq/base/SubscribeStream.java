package org.yourmq.base;

public interface SubscribeStream extends Stream<SubscribeStream> {
    SubscribeStream thenReply(IoConsumer<Reply> onReply);
}

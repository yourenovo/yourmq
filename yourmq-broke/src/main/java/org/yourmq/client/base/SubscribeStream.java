package org.yourmq.client.base;

public interface SubscribeStream extends Stream<SubscribeStream> {
    SubscribeStream thenReply(IoConsumer<Reply> onReply);
}

package org.yourmq.client.base;

public interface RequestStream extends Stream<RequestStream> {
    Reply await();

    RequestStream thenReply(IoConsumer<Reply> onReply);
}

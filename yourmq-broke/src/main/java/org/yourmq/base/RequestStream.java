package org.yourmq.base;

public interface RequestStream extends Stream<RequestStream> {
    Reply await();

    RequestStream thenReply(IoConsumer<Reply> onReply);
}

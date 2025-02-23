
package org.yourmq.base;

public class SubscribeStreamImpl extends StreamBase<SubscribeStream> implements SubscribeStream {
    private IoConsumer<Reply> doOnReply;
    private boolean isDone;

    public SubscribeStreamImpl(String sid, long timeout) {
        super(sid, 2, timeout);
    }

    @Override
    public boolean isDone() {
        return this.isDone;
    }

    @Override
    public void onReply(MessageInternal reply) {
        this.isDone = reply.isEnd();

        try {
            if (this.doOnReply != null) {
                this.doOnReply.accept(reply);
            }
        } catch (Throwable var3) {
            this.onError(var3);
        }

    }

    @Override

    public SubscribeStream thenReply(IoConsumer<Reply> onReply) {
        this.doOnReply = onReply;
        return this;
    }
}

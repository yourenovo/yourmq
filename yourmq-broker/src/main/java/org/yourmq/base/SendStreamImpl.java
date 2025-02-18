package org.yourmq.base;

public class SendStreamImpl extends StreamBase<SendStream> implements SendStream {
    public SendStreamImpl(String sid) {
        super(sid, 0, 10L);
    }

    public boolean isDone() {
        return true;
    }

    public void onReply(MessageInternal reply) {
    }
}


package org.yourmq.base;

import org.yourmq.exception.YourSocketException;
import org.yourmq.exception.YourSocketTimeoutException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RequestStreamImpl extends StreamBase<RequestStream> implements RequestStream {
    private final CompletableFuture<Reply> future = new CompletableFuture();

    public RequestStreamImpl(String sid, long timeout) {
        super(sid, 1, timeout);
    }
    @Override
    public boolean isDone() {
        return this.future.isDone();
    }
    @Override
    public void onError(Throwable error) {
        super.onError(error);
        this.future.completeExceptionally(error);
    }
    @Override
    public void onReply(MessageInternal reply) {
        this.future.complete(reply);
    }
    @Override
    public Reply await() {
        try {
            return (Reply)this.future.get(this.timeout(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException var2) {
            throw new YourSocketTimeoutException("Request reply timeout > " + this.timeout() + ", sid=" + this.sid());
        } catch (Throwable var3) {
            Throwable e = var3;
            if (var3 instanceof ExecutionException) {
                e = var3.getCause();
            }

            if (e instanceof YourSocketException) {
                throw (YourSocketException)e;
            } else {
                throw new YourSocketException("Request failed, sid=" + this.sid(), e);
            }
        }
    }
@Override
    public RequestStream thenReply(IoConsumer<Reply> onReply) {
        this.future.thenAccept((r) -> {
            try {
                onReply.accept(r);
            } catch (Throwable var4) {
                this.onError(var4);
            }

        });
        return this;
    }
}

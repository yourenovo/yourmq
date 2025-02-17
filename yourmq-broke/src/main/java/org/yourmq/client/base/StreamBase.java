//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.yourmq.client.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yourmq.common.YourSocketTimeoutException;
import org.yourmq.utils.RunUtils;

import java.util.concurrent.ScheduledFuture;
import java.util.function.Consumer;

public abstract class StreamBase<T extends Stream> implements StreamInternal<T> {
    private static final Logger log = LoggerFactory.getLogger(Stream.class);
    private ScheduledFuture<?> insuranceFuture;
    private final String sid;
    private final int demands;
    private long timeout;
    protected Consumer<Throwable> doOnError;
    protected TriConsumer<Boolean, Integer, Integer> doOnProgress;

    public StreamBase(String sid, int demands, long timeout) {
        this.sid = sid;
        this.demands = demands;
        this.timeout = timeout;
    }
    @Override
    public String sid() {
        return this.sid;
    }
@Override
    public int demands() {
        return this.demands;
    }

    public T timeout(long timeout) {
        this.timeout = timeout;
        return (T) this;
    }
    @Override
    public long timeout() {
        return this.timeout;
    }
    @Override
    public void insuranceStart(StreamManger streamManger, long streamTimeout) {
        if (this.insuranceFuture == null) {
            this.insuranceFuture = RunUtils.delay(() -> {
                streamManger.removeStream(this.sid);
                this.onError(new YourSocketTimeoutException("The stream response timeout, sid=" + this.sid));
            }, streamTimeout);
        }
    }
    @Override
    public void insuranceCancel() {
        if (this.insuranceFuture != null) {
            this.insuranceFuture.cancel(false);
        }

    }
    @Override
    public void onError(Throwable error) {
        if (this.doOnError != null) {
            this.doOnError.accept(error);
        } else if (log.isDebugEnabled()) {
            log.debug("The stream error, sid={}", this.sid(), error);
        }

    }
    @Override
    public void onProgress(boolean isSend, int val, int max) {
        if (this.doOnProgress != null) {
            this.doOnProgress.accept(isSend, val, max);
        }

    }
    @Override
    public T thenError(Consumer<Throwable> onError) {
        this.doOnError = onError;
        return (T) this;
    }
    @Override
    public T thenProgress(TriConsumer<Boolean, Integer, Integer> onProgress) {
        this.doOnProgress = onProgress;
        return (T) this;
    }
}

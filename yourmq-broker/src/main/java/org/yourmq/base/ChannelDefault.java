
package org.yourmq.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;

public class ChannelDefault<S> extends ChannelBase implements ChannelInternal {
    private static Logger log = LoggerFactory.getLogger(ChannelDefault.class);
    private final S source;
    private final Processor processor;
    private final ChannelAssistant<S> assistant;
    private final StreamManger streamManger;
    private final ReentrantLock sendInFairLock;
    private final ReentrantLock sendNoFairLock;
    private Session session;
    private long liveTime;
    private BiConsumer<Boolean, Throwable> onOpenFuture;
    private int closeCode;
    private int alarmCode;
    private AtomicBoolean isCloseNotified = new AtomicBoolean(false);

    public ChannelDefault(S source, ChannelSupporter<S> supporter) {
        super(supporter.getConfig());
        this.source = source;
        this.processor = supporter.getProcessor();
        this.assistant = (ChannelAssistant<S>) supporter.getAssistant();
        this.streamManger = supporter.getConfig().getStreamManger();
        this.sendInFairLock = new ReentrantLock(true);
        this.sendNoFairLock = new ReentrantLock(false);
    }

    @Override
    public boolean isValid() {
        return this.closeCode() == 0 && this.assistant.isValid(this.source);
    }

    @Override
    public boolean isClosing() {
        return this.closeCode == 1000;
    }

    @Override
    public int closeCode() {
        return this.closeCode > 1000 ? this.closeCode : 0;
    }

    @Override
    public long getLiveTime() {
        return this.liveTime;
    }

    @Override
    public void setLiveTimeAsNow() {
        this.liveTime = System.currentTimeMillis();
    }

    @Override
    public void setAlarmCode(int alarmCode) {
        this.alarmCode = alarmCode;
    }

    @Override
    public InetSocketAddress getRemoteAddress() throws IOException {
        return this.assistant.getRemoteAddress(this.source);
    }

    @Override
    public InetSocketAddress getLocalAddress() throws IOException {
        return this.assistant.getLocalAddress(this.source);
    }

    @Override
    public void send(Frame frame, StreamInternal stream) throws IOException {
        if (log.isDebugEnabled()) {
            if (this.getConfig().clientMode()) {
                log.debug("C-SEN:{}", frame);
            } else {
                log.debug("S-SEN:{}", frame);
            }
        }

        boolean isSerialSend = this.getConfig().isSerialSend();
        if (isSerialSend) {
            this.sendInFairLock.lock();

            try {
                this.sendDo(frame, stream);
            } finally {
                this.sendInFairLock.unlock();
            }
        } else {
            this.sendNoFairLock.lock();

            try {
                this.sendDo(frame, stream);
            } finally {
                this.sendNoFairLock.unlock();
            }
        }

    }

    private void sendDo(Frame frame, StreamInternal stream) throws IOException {
        if (this.alarmCode == 3001 && frame.flag() >= 40 && frame.flag() <= 42 && frame.message().meta("X-Unlimited") == null) {
            try {
                if (log.isDebugEnabled()) {
                    log.debug("Too much pressure, sleep=100ms");
                }

                this.setAlarmCode(0);
                Thread.sleep(100L);
            } catch (Throwable var4) {
            }
        }

        if (frame.message() != null) {
            MessageInternal message = frame.message();
            if (stream != null) {
                this.streamManger.addStream(message.sid(), stream);
            }

            if (message.entity() != null) {
                if (message.dataSize() > this.getConfig().getFragmentSize()) {
                    message.putMeta("Data-Length", String.valueOf(message.dataSize()));
                }

                this.getConfig().getFragmentHandler().spliFragment(this, stream, message, (fragmentEntity) -> {
                    Frame fragmentFrame;
                    if (fragmentEntity instanceof MessageInternal) {
                        fragmentFrame = new Frame(frame.flag(), (MessageInternal) fragmentEntity);
                    } else {
                        fragmentFrame = new Frame(frame.flag(), (new MessageBuilder()).flag(frame.flag()).sid(message.sid()).event(message.event()).entity(fragmentEntity).build());
                    }

                    this.processor.sendFrame(this, fragmentFrame, this.assistant, this.source);
                });
                return;
            }
        }

        this.processor.sendFrame(this, frame, this.assistant, this.source);
        if (stream != null) {
            stream.onProgress(true, 1, 1);
        }

    }

    @Override
    public void reconnect() throws IOException {
    }

    @Override
    public void onError(Throwable error) {
        this.processor.onError(this, error);
    }

    @Override
    public Session getSession() {
        if (this.session == null) {
            this.session = new SessionDefault(this);
        }

        return this.session;
    }

    @Override
    public void setSession(Session session) {
        this.session = session;
    }

    @Override
    public StreamInternal getStream(String sid) {
        return this.streamManger.getStream(sid);
    }

    @Override
    public void onOpenFuture(BiConsumer<Boolean, Throwable> future) {
        this.onOpenFuture = future;
    }

    @Override
    public void doOpenFuture(boolean isOk, Throwable error) {
        if (this.onOpenFuture != null) {
            this.onOpenFuture.accept(isOk, error);
        }

    }

    @Override
    public void close(int code) {
        try {
            this.closeCode = code;
            super.close(code);
            if (code > 1000 && this.assistant.isValid(this.source)) {
                this.assistant.close(this.source);
                if (log.isDebugEnabled()) {
                    log.debug("{} channel closed, sessionId={}", this.getConfig().getRoleName(), this.getSession().sessionId());
                }
            }
        } catch (Throwable var3) {
            if (log.isWarnEnabled()) {
                log.warn("{} channel close error, sessionId={}", new Object[]{this.getConfig().getRoleName(), this.getSession().sessionId(), var3});
            }
        }

        if (code > 1000) {
            this.onCloseDo();
        }

    }

    private void onCloseDo() {
        if (this.isCloseNotified.compareAndSet(false, true)) {
            this.processor.doCloseNotice(this);
        }

    }
}

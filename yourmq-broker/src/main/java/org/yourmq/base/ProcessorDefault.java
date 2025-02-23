
package org.yourmq.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yourmq.common.YourMQConnectionException;
import org.yourmq.exception.YourSocketAlarmException;
import org.yourmq.exception.YourSocketException;
import org.yourmq.utils.MemoryUtils;

import java.io.IOException;
import java.io.NotActiveException;

public class ProcessorDefault implements Processor, FrameIoHandler {
    private static Logger log = LoggerFactory.getLogger(ProcessorDefault.class);
    private Listener listener = new SimpleListener();

    public ProcessorDefault() {
    }
@Override
    public void setListener(Listener listener) {
        if (listener != null) {
            this.listener = listener;
        }

    }
   @Override
    public <S> void sendFrame(ChannelInternal channel, Frame frame, ChannelAssistant<S> channelAssistant, S target) throws IOException {
        if (frame != null) {
            if (!channel.isValid()) {
                throw new NotActiveException("Channel is invalid");
            } else {
                IoCompletionHandlerImpl completionHandler = new IoCompletionHandlerImpl();
                if (channel.getConfig().getTrafficLimiter() == null) {
                    this.sendFrameHandle(channel, frame, channelAssistant, target, completionHandler);
                } else {
                    channel.getConfig().getTrafficLimiter().sendFrame(this, channel, frame, channelAssistant, target, completionHandler);
                }

                if (completionHandler.getThrowable() != null) {
                    if (completionHandler.getThrowable() instanceof IOException) {
                        throw (IOException)completionHandler.getThrowable();
                    } else {
                        throw new YourSocketException("Channel send failure", completionHandler.getThrowable());
                    }
                }
            }
        }
    }
    @Override
    public <S> void sendFrameHandle(ChannelInternal channel, Frame frame, ChannelAssistant<S> channelAssistant, S target, IoCompletionHandler completionHandler) {
        try {
            channelAssistant.write(target, frame, channel, completionHandler);
            if (frame.flag() >= 40) {
                this.listener.onSend(channel.getSession(), frame.message());
            }
        } catch (Throwable var7) {
            completionHandler.completed(false, var7);
        }

    }
@Override
    public void reveFrame(ChannelInternal channel, Frame frame) {
        if (channel.getConfig().getTrafficLimiter() == null) {
            this.reveFrameHandle(channel, frame);
        } else {
            channel.getConfig().getTrafficLimiter().reveFrame(this, channel, frame);
        }

    }
    @Override
    public void reveFrameHandle(ChannelInternal channel, Frame frame) {
        if (log.isDebugEnabled()) {
            if (channel.getConfig().clientMode()) {
                log.debug("C-REV:{}", frame);
            } else {
                log.debug("S-REV:{}", frame);
            }
        }

        HandshakeDefault handshake;
        if (frame.flag() == 10) {
            handshake = new HandshakeDefault(frame.message());
            channel.setHandshake(handshake);
            channel.onOpenFuture((r, e) -> {
                if (r) {
                    if (channel.isValid()) {
                        try {
                            channel.sendConnack();
                        } catch (Throwable var5) {
                            this.onError(channel, var5);
                        }
                    }
                } else if (channel.isValid()) {
                    this.onCloseInternal(channel, 2001);
                }

            });
            this.onOpen(channel);
        } else if (frame.flag() == 11) {
            handshake = new HandshakeDefault(frame.message());
            channel.setHandshake(handshake);
            this.onOpen(channel);
        } else {
            if (channel.getHandshake() == null) {
                channel.close(1002);
                if (frame.flag() == 30) {
                    throw new YourMQConnectionException("Connection request was rejected");
                }

                if (log.isWarnEnabled()) {
                    log.warn("{} channel handshake is null, sessionId={}", channel.getConfig().getRoleName(), channel.getSession().sessionId());
                }

                return;
            }

            channel.setLiveTimeAsNow();

            try {
                int code;
                switch (frame.flag()) {
                    case 20:
                        this.callAsync(channel, () -> {
                            channel.sendPong();
                        });
                    case 21:
                        break;
                    case 22:
                    case 23:
                    case 24:
                    case 25:
                    case 26:
                    case 27:
                    case 28:
                    case 29:
                    case 33:
                    case 34:
                    case 35:
                    case 36:
                    case 37:
                    case 38:
                    case 39:
                    case 43:
                    case 44:
                    case 45:
                    case 46:
                    case 47:
                    default:
                        this.onCloseInternal(channel, 1002);
                        break;
                    case 30:
                        code = 0;
                        if (frame.message() != null) {
                            code = frame.message().metaAsInt("code");
                        }

                        if (code == 0) {
                            code = 1001;
                        }

                        this.onCloseInternal(channel, code);
                        break;
                    case 31:
                        YourSocketAlarmException exception = new YourSocketAlarmException(frame.message());
                        channel.setAlarmCode(exception.getAlarmCode());
                        StreamInternal stream = channel.getStream(frame.message().sid());
                        if (stream == null) {
                            this.callAsync(channel, () -> {
                                this.onError(channel, exception);
                            });
                        } else {
                            channel.getConfig().getStreamManger().removeStream(frame.message().sid());
                            this.callAsync(channel, () -> {
                                stream.onError(exception);
                            });
                        }
                        break;
                    case 32:
                        code = frame.message().metaAsInt("code");
                        channel.setAlarmCode(code);
                        break;
                    case 40:
                    case 41:
                    case 42:
                        if (this.chkMemoryLimit(channel, frame)) {
                            this.onReceiveDo(channel, frame, false);
                        }
                        break;
                    case 48:
                    case 49:
                        this.onReceiveDo(channel, frame, true);
                }
            } catch (Throwable var5) {
                this.onError(channel, var5);
            }
        }

    }

    private boolean chkMemoryLimit(ChannelInternal channel, Frame frame) {
        if (channel.getConfig().useMaxMemoryLimit()) {
            float useMemoryRatio = MemoryUtils.getUseMemoryRatio();
            if (useMemoryRatio > channel.getConfig().getMaxMemoryRatio()) {
                if (frame.message().meta("X-Unlimited") == null) {
                    try {
                        String alarm = String.format(" memory usage over limit: %.2f%%", useMemoryRatio * 100.0F);
                        if (log.isDebugEnabled()) {
                            log.debug("Local " + alarm + ", frame: " + frame);
                        }

                        PressureEntity pressure = new PressureEntity(channel.getConfig().getRoleName() + alarm);
                        channel.sendAlarm(frame.message(), pressure);
                    } catch (Throwable var6) {
                        this.onError(channel, var6);
                    }

                    return false;
                }

                return true;
            }
        }

        return true;
    }

    private void onReceiveDo(ChannelInternal channel, Frame frame, boolean isReply) throws IOException {
        StreamInternal stream = null;
        int streamIndex = 0;
        int streamTotal = 1;
        if (isReply) {
            stream = channel.getStream(frame.message().sid());
        }

        if (channel.getConfig().getFragmentHandler().aggrEnable()) {
            String fragmentIdxStr = frame.message().meta("Data-Fragment-Idx");
            if (fragmentIdxStr != null) {
                streamIndex = Integer.parseInt(fragmentIdxStr);
                Frame frameNew = channel.getConfig().getFragmentHandler().aggrFragment(channel, streamIndex, frame.message());
                if (stream != null) {
                    streamTotal = Integer.parseInt(frame.message().metaOrDefault("Data-Fragment-Total", "0"));
                }

                if (frameNew == null) {
                    if (stream != null) {
                        stream.onProgress(false, streamIndex, streamTotal);
                    }

                    return;
                }

                frame = frameNew;
            }
        }

        if (isReply) {
            if (stream != null) {
                stream.onProgress(false, streamIndex, streamTotal);
            }

            this.onReply(channel, frame, stream);
        } else {
            this.onMessage(channel, frame);
        }

    }
@Override
    public void onOpen(ChannelInternal channel) {
        this.callAsync(channel, () -> {
            try {
                this.listener.onOpen(channel.getSession());
                channel.doOpenFuture(true, (Throwable)null);
            } catch (Throwable var3) {
                if (log.isWarnEnabled()) {
                    log.warn("{} channel listener onOpen error", channel.getConfig().getRoleName(), var3);
                }

                channel.doOpenFuture(false, var3);
            }

        });
    }
    @Override
    public void onMessage(ChannelInternal channel, Frame frame) {
        this.callAsync(channel, () -> {
            try {
                this.listener.onMessage(channel.getSession(), frame.message());
            } catch (Throwable var4) {
                if (log.isWarnEnabled()) {
                    log.warn("{} channel listener onMessage error", channel.getConfig().getRoleName(), var4);
                }

                this.onError(channel, var4);
            }

        });
    }
    @Override
    public void onReply(ChannelInternal channel, Frame frame, StreamInternal stream) {
        if (stream != null) {
            if (stream.demands() < 2 || frame.flag() == 49) {
                channel.getConfig().getStreamManger().removeStream(frame.message().sid());
            }

            if (stream.demands() < 2) {
                stream.onReply(frame.message());
            }

            this.callAsync(channel, () -> {
                if (stream.demands() == 2) {
                    stream.onReply(frame.message());
                }

                this.listener.onReply(channel.getSession(), frame.message());
            });
        } else {
            this.callAsync(channel, () -> {
                this.listener.onReply(channel.getSession(), frame.message());
            });
            if (log.isDebugEnabled()) {
                log.debug("{} stream not found, sid={}, sessionId={}", new Object[]{channel.getConfig().getRoleName(), frame.message().sid(), channel.getSession().sessionId()});
            }
        }

    }
    @Override
    public void onClose(ChannelInternal channel) {
        if (channel.closeCode() <= 1000) {
            this.onCloseInternal(channel, 2003);
        }

    }

    private void onCloseInternal(ChannelInternal channel, int code) {
        channel.close(code);
    }
    @Override
    public void onError(ChannelInternal channel, Throwable error) {
        try {
            this.listener.onError(channel.getSession(), error);
        } catch (Throwable var4) {
            if (log.isWarnEnabled()) {
                log.warn("{} channel listener onError error", channel.getConfig().getRoleName(), var4);
            }
        }

    }
    @Override
    public void doCloseNotice(ChannelInternal channel) {
        try {
            if (channel.getHandshake() != null) {
                this.listener.onClose(channel.getSession());
            }
        } catch (Throwable var3) {
            this.onError(channel, var3);
        }

    }

    private void callAsync(ChannelInternal channel, RunnableEx<Throwable> runnable) {
        try {
            channel.getConfig().getWorkExecutor().submit(() -> {
                try {
                    runnable.run();
                } catch (Throwable var4) {
                    this.onError(channel, var4);
                }

            });
        } catch (Throwable var4) {
            this.onError(channel, var4);
        }

    }
}

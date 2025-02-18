package org.yourmq.base;

import java.io.IOException;

public interface Processor {
    void setListener(Listener listener);

    <S> void sendFrame(ChannelInternal channel, Frame frame, ChannelAssistant<S> channelAssistant, S target) throws IOException;

    void reveFrame(ChannelInternal channel, Frame frame);

    /** @deprecated */
    @Deprecated
    default void onReceive(ChannelInternal channel, Frame frame) {
        this.reveFrame(channel, frame);
    }

    void onOpen(ChannelInternal channel);

    void onMessage(ChannelInternal channel, Frame frame);

    void onReply(ChannelInternal channel, Frame frame, StreamInternal stream);

    void onClose(ChannelInternal channel);

    void onError(ChannelInternal channel, Throwable error);

    void doCloseNotice(ChannelInternal channel);
}

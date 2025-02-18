package org.yourmq.base;

public interface FrameIoHandler {
    <S> void sendFrameHandle(ChannelInternal channel, Frame frame, ChannelAssistant<S> channelAssistant, S target, IoCompletionHandler completionHandler);

    void reveFrameHandle(ChannelInternal channel, Frame frame);
}
package org.yourmq.client.base;

public interface TrafficLimiter {
    <S> void sendFrame(FrameIoHandler frameIoHandler, ChannelInternal channel, Frame frame, ChannelAssistant<S> channelAssistant, S target, IoCompletionHandler completionHandler);

    void reveFrame(FrameIoHandler frameIoHandler, ChannelInternal channel, Frame frame);
}

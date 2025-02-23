
package org.yourmq.base;

import org.yourmq.utils.RunUtils;

public class TrafficLimiterDefault implements TrafficLimiter {
    private int sendRate;
    private int receRate;
    private final long interval;
    private volatile int sendCount;
    private volatile int receCount;
    private volatile long sendLatestLimitTime;
    private volatile long receLatestLimitTime;
    private long receLatestTime;
    private long sendLatestTime;

    public int getSendRate() {
        return this.sendRate;
    }

    public void setSendRate(int sendRate) {
        this.sendRate = sendRate;
    }

    public int getReceRate() {
        return this.receRate;
    }

    public void setReceRate(int receRate) {
        this.receRate = receRate;
    }

    public TrafficLimiterDefault(int sendAndReceRate) {
        this(sendAndReceRate, sendAndReceRate);
    }

    public TrafficLimiterDefault(int sendRate, int receRate) {
        this.interval = 1000L;
        this.sendLatestLimitTime = Long.MIN_VALUE;
        this.receLatestLimitTime = Long.MIN_VALUE;
        this.receLatestTime = Long.MIN_VALUE;
        this.sendLatestTime = Long.MIN_VALUE;
        this.sendRate = sendRate;
        this.receRate = receRate;
    }

    @Override
    public <S> void sendFrame(FrameIoHandler frameIoHandler, ChannelInternal channel, Frame frame, ChannelAssistant<S> channelAssistant, S target, IoCompletionHandler completionHandler) {
        if (this.sendRate < 1) {
            frameIoHandler.sendFrameHandle(channel, frame, channelAssistant, target, completionHandler);
        } else {
            if (this.sendLatestTime >= this.sendLatestLimitTime) {
                this.sendCount = 0;
                this.sendLatestLimitTime = RunUtils.milliSecondFromNano() + 1000L;
            }

            if (this.sendCount < this.sendRate) {
                ++this.sendCount;
                frameIoHandler.sendFrameHandle(channel, frame, channelAssistant, target, completionHandler);
            } else {
                this.sendLatestTime = RunUtils.milliSecondFromNano();
                if (this.sendLatestTime < this.sendLatestLimitTime) {
                    try {
                        Thread.sleep(this.sendLatestLimitTime - this.sendLatestTime);
                    } catch (Throwable var8) {
                        return;
                    }
                }

                this.sendFrame(frameIoHandler, channel, frame, channelAssistant, target, completionHandler);
            }

        }
    }

    @Override
    public void reveFrame(FrameIoHandler frameIoHandler, ChannelInternal channel, Frame frame) {
        if (this.receRate < 1) {
            frameIoHandler.reveFrameHandle(channel, frame);
        } else {
            if (this.receLatestTime >= this.receLatestLimitTime) {
                this.receCount = 0;
                this.receLatestLimitTime = RunUtils.milliSecondFromNano() + 1000L;
            }

            if (this.receCount < this.receRate) {
                ++this.receCount;
                frameIoHandler.reveFrameHandle(channel, frame);
            } else {
                this.receLatestTime = RunUtils.milliSecondFromNano();
                if (this.receLatestTime < this.receLatestLimitTime) {
                    try {
                        Thread.sleep(this.receLatestLimitTime - this.receLatestTime);
                    } catch (Throwable var5) {
                        return;
                    }
                }

                this.reveFrame(frameIoHandler, channel, frame);
            }

        }
    }
}

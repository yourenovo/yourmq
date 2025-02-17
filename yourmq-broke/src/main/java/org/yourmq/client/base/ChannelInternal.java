package org.yourmq.client.base;

import java.util.function.BiConsumer;

public interface ChannelInternal extends Channel {
    void setSession(Session session);

    void setLiveTimeAsNow();

    void setAlarmCode(int alarmCode);

    StreamInternal getStream(String sid);

    void onOpenFuture(BiConsumer<Boolean, Throwable> future);

    void doOpenFuture(boolean isOk, Throwable error);
}
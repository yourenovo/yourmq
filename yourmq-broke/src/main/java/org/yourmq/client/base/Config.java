package org.yourmq.client.base;

import javax.net.ssl.SSLContext;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;

public interface Config {
    boolean clientMode();

    boolean isSerialSend();

    /** @deprecated */
    @Deprecated
    boolean isNolockSend();

    StreamManger getStreamManger();

    String getRoleName();

    Charset getCharset();

    Codec getCodec();

    String genId();

    FragmentHandler getFragmentHandler();

    int getFragmentSize();

    SSLContext getSslContext();

    int getIoThreads();

    int getCodecThreads();

    int getWorkThreads();

    ExecutorService getWorkExecutor();

    /** @deprecated */
    @Deprecated
    default int getExchangeThreads() {
        return this.getWorkThreads();
    }

    /** @deprecated */
    @Deprecated
    default ExecutorService getExchangeExecutor() {
        return this.getWorkExecutor();
    }

    int getReadBufferSize();

    int getWriteBufferSize();

    long getIdleTimeout();

    long getRequestTimeout();

    long getStreamTimeout();

    int getMaxUdpSize();

    boolean useMaxMemoryLimit();

    float getMaxMemoryRatio();

    TrafficLimiter getTrafficLimiter();

    boolean isUseSubprotocols();
}

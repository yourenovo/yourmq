
package org.yourmq.inter;

import org.yourmq.base.*;

import javax.net.ssl.SSLContext;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public abstract class ConfigBase<T extends Config> implements Config {
    private final boolean clientMode;
    private boolean serialSend;
    private boolean nolockSend;
    private final StreamManger streamManger;
    private final Codec codec;
    private IdGenerator idGenerator;
    private FragmentHandler fragmentHandler;
    private int fragmentSize;
    private SSLContext sslContext;
    protected Charset charset;
    protected int ioThreads;
    protected int codecThreads;
    protected int workThreads;
    private volatile ExecutorService workExecutor;
    private volatile ExecutorService workExecutorSelfNew;
    protected int readBufferSize;
    protected int writeBufferSize;
    protected long idleTimeout;
    protected long requestTimeout;
    protected long streamTimeout;
    protected int maxUdpSize;
    private boolean useMaxMemoryLimit;
    protected float maxMemoryRatio;
    protected TrafficLimiter trafficLimiter;
    protected boolean useSubprotocols;
    private ReentrantLock EXECUTOR_LOCK = new ReentrantLock();

    public ConfigBase(boolean clientMode) {
        this.clientMode = clientMode;
        this.serialSend = false;
        this.nolockSend = false;
        this.streamManger = new StreamMangerDefault(this);
        this.codec = new CodecDefault(this);
        this.charset = StandardCharsets.UTF_8;
        this.idGenerator = new GuidGenerator();
        this.fragmentHandler = new FragmentHandlerDefault();
        this.fragmentSize = 16777216;
        this.ioThreads = 1;
        this.codecThreads = Runtime.getRuntime().availableProcessors();
        this.workThreads = Runtime.getRuntime().availableProcessors() * 4;
        this.readBufferSize = 8192;
        this.writeBufferSize = 8192;
        this.idleTimeout = 60000L;
        this.requestTimeout = 10000L;
        this.streamTimeout = 7200000L;
        this.maxUdpSize = 2048;
        this.maxMemoryRatio = 0.0F;
        this.useMaxMemoryLimit = false;
        this.useSubprotocols = true;
    }

    @Override
    public boolean clientMode() {
        return this.clientMode;
    }

    @Override
    public boolean isSerialSend() {
        return this.serialSend;
    }

    public T serialSend(boolean serialSend) {
        this.serialSend = serialSend;
        return (T) this;
    }

    @Override
    public boolean isNolockSend() {
        return this.nolockSend;
    }

    public T nolockSend(boolean nolockSend) {
        this.nolockSend = nolockSend;
        return (T) this;
    }

    @Override
    public StreamManger getStreamManger() {
        return this.streamManger;
    }

    @Override
    public String getRoleName() {
        return this.clientMode() ? "Client" : "Server";
    }
    @Override
    public Charset getCharset() {
        return this.charset;
    }

    public T charset(Charset charset) {
        this.charset = charset;
        return (T) this;
    }

    @Override
    public Codec getCodec() {
        return this.codec;
    }

    @Override
    public FragmentHandler getFragmentHandler() {
        return this.fragmentHandler;
    }

    public T fragmentHandler(FragmentHandler fragmentHandler) {
        ;
        this.fragmentHandler = fragmentHandler;
        return (T) this;
    }
    @Override
    public int getFragmentSize() {
        return this.fragmentSize;
    }

    public T fragmentSize(int fragmentSize) {
        if (fragmentSize > 16777216) {
            throw new IllegalArgumentException("The parameter fragmentSize cannot > 16m");
        } else if (fragmentSize < 1024) {
            throw new IllegalArgumentException("The parameter fragmentSize cannot < 1k");
        } else {
            this.fragmentSize = fragmentSize;
            return (T) this;
        }
    }
    @Override
    public String genId() {
        return this.idGenerator.generate();
    }

    public T idGenerator(IdGenerator idGenerator) {
        ;
        this.idGenerator = idGenerator;
        return (T) this;
    }
    @Override
    public SSLContext getSslContext() {
        return this.sslContext;
    }

    public T sslContext(SSLContext sslContext) {
        this.sslContext = sslContext;
        return (T) this;
    }
    @Override
    public ExecutorService getWorkExecutor() {
        if (this.workExecutor == null) {
            this.EXECUTOR_LOCK.lock();

            try {
                if (this.workExecutor == null) {
                    int nThreads = this.getWorkThreads();
                    this.workExecutor = this.workExecutorSelfNew = new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue(), (new NamedThreadFactory("Socketd-work-")).daemon(true));
                }
            } finally {
                this.EXECUTOR_LOCK.unlock();
            }
        }

        return this.workExecutor;
    }

    public T workExecutor(ExecutorService workExecutor) {
        this.workExecutor = workExecutor;
        if (this.workExecutorSelfNew != null) {
            this.workExecutorSelfNew.shutdown();
        }

        return (T) this;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public T exchangeExecutor(ExecutorService workExecutor) {
        return this.workExecutor(workExecutor);
    }
    @Override
    public int getIoThreads() {
        return this.ioThreads;
    }

    public T ioThreads(int ioThreads) {
        this.ioThreads = ioThreads;
        return (T) this;
    }
    @Override
    public int getCodecThreads() {
        return this.codecThreads;
    }

    public T codecThreads(int codecThreads) {
        this.codecThreads = codecThreads;
        return (T) this;
    }
    @Override
    public int getWorkThreads() {
        return this.workThreads;
    }

    public T workThreads(int workThreads) {
        this.workThreads = workThreads;
        return (T) this;
    }

    /** @deprecated */
    @Deprecated
    public T exchangeThreads(int workThreads) {
        return this.workThreads(workThreads);
    }
    @Override
    public int getReadBufferSize() {
        return this.readBufferSize;
    }

    public T readBufferSize(int readBufferSize) {
        this.readBufferSize = readBufferSize;
        return (T) this;
    }
    @Override
    public int getWriteBufferSize() {
        return this.writeBufferSize;
    }

    public T writeBufferSize(int writeBufferSize) {
        this.writeBufferSize = writeBufferSize;
        return (T) this;
    }
    @Override
    public long getIdleTimeout() {
        return this.idleTimeout;
    }

    public T idleTimeout(int idleTimeout) {
        this.idleTimeout = (long)idleTimeout;
        return (T) this;
    }
    @Override
    public long getRequestTimeout() {
        return this.requestTimeout;
    }

    public T requestTimeout(long requestTimeout) {
        this.requestTimeout = requestTimeout;
        return (T) this;
    }
    @Override
    public long getStreamTimeout() {
        return this.streamTimeout;
    }

    public T streamTimeout(long streamTimeout) {
        this.streamTimeout = streamTimeout;
        return (T) this;
    }
    @Override
    public int getMaxUdpSize() {
        return this.maxUdpSize;
    }

    public T maxUdpSize(int maxUdpSize) {
        this.maxUdpSize = maxUdpSize;
        return (T) this;
    }

    @Override
    public boolean useMaxMemoryLimit() {
        return this.useMaxMemoryLimit;
    }

    @Override
    public float getMaxMemoryRatio() {
        return this.maxMemoryRatio;
    }

    public T maxMemoryRatio(float maxMemoryRatio) {
        this.maxMemoryRatio = maxMemoryRatio;
        this.useMaxMemoryLimit = maxMemoryRatio > 0.2F;
        return (T) this;
    }

    public TrafficLimiter getTrafficLimiter() {
        return this.trafficLimiter;
    }

    public T trafficLimiter(TrafficLimiter trafficLimiter) {
        this.trafficLimiter = trafficLimiter;
        return (T) this;
    }

    public T useSubprotocols(boolean useSubprotocols) {
        this.useSubprotocols = useSubprotocols;
        return (T) this;
    }
    @Override
    public boolean isUseSubprotocols() {
        return this.useSubprotocols;
    }
}

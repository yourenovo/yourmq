//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.yourmq.base;

public abstract class BaseServerProps {
    private String PROP_NAME = "server.@@.name";
    private String PROP_PORT = "server.@@.port";
    private String PROP_HOST = "server.@@.host";
    private String PROP_WRAP_PORT = "server.@@.wrapPort";
    private String PROP_WRAP_HOST = "server.@@.wrapHost";
    private String PROP_IO_BOUND = "server.@@.ioBound";
    private String PROP_CORE_THREADS = "server.@@.coreThreads";
    private String PROP_MAX_THREADS = "server.@@.maxThreads";
    private String PROP_IDLE_TIMEOUT = "server.@@.idleTimeout";
    private String name;
    private int port;
    private String host;
    private int wrapPort;
    private String wrapHost;
    private boolean ioBound;
    private int coreThreads;
    private int maxThreads;
    private long idleTimeout;

    protected BaseServerProps(String signalName, int portBase) {
        this.PROP_NAME = this.PROP_NAME.replace("@@", signalName);
        this.PROP_PORT = this.PROP_PORT.replace("@@", signalName);
        this.PROP_HOST = this.PROP_HOST.replace("@@", signalName);
        this.PROP_WRAP_PORT = this.PROP_WRAP_PORT.replace("@@", signalName);
        this.PROP_WRAP_HOST = this.PROP_WRAP_HOST.replace("@@", signalName);
        this.PROP_IO_BOUND = this.PROP_IO_BOUND.replace("@@", signalName);
        this.PROP_CORE_THREADS = this.PROP_CORE_THREADS.replace("@@", signalName);
        this.PROP_MAX_THREADS = this.PROP_MAX_THREADS.replace("@@", signalName);
        this.PROP_IDLE_TIMEOUT = this.PROP_IDLE_TIMEOUT.replace("@@", signalName);
        this.initSignalProps(portBase);
        this.initExecutorProps();
    }

    private void initSignalProps(int portBase) {
    }

    public String getName() {
        return this.name;
    }

    public int getPort() {
        return this.port;
    }

    public String getHost() {
        return this.host;
    }

    public int getWrapPort() {
        return this.wrapPort;
    }

    public String getWrapHost() {
        return this.wrapHost;
    }

    private void initExecutorProps() {
    }

    public boolean isIoBound() {
        return this.ioBound;
    }

    private int getCoreNum() {
        return Runtime.getRuntime().availableProcessors();
    }

    public int getCoreThreads() {
        return this.coreThreads > 0 ? this.coreThreads : Math.max(this.getCoreNum(), 2);
    }

    public int getMaxThreads(boolean isIoBound) {
        if (this.maxThreads > 0) {
            return this.maxThreads;
        } else {
            return isIoBound ? this.getCoreThreads() * 32 : this.getCoreThreads() * 8;
        }
    }

    public long getIdleTimeout() {
        return this.idleTimeout;
    }

    public long getIdleTimeoutOrDefault() {
        return this.idleTimeout > 0L ? this.idleTimeout : 300000L;
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.yourmq.base;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yourmq.common.Entity;
import org.yourmq.common.Message;
import org.yourmq.exception.YourSocketChannelException;
import org.yourmq.exception.YourSocketException;
import org.yourmq.utils.RunUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientChannel extends ChannelBase implements Channel {
    private static final Logger log = LoggerFactory.getLogger(ClientChannel.class);
    private final ClientInternal client;
    private final ClientConnector connector;
    private final Session sessionShell;
    private ChannelInternal real;
    private ClientHeartbeatHandler heartbeatHandler;
    private ScheduledFuture<?> heartbeatScheduledFuture;
    private AtomicBoolean isConnecting = new AtomicBoolean(false);

    public ClientChannel(ClientInternal client, ClientConnector connector) {
        super(connector.getConfig());
        this.client = client;
        this.connector = connector;
        this.sessionShell = new SessionDefault(this);
        if (client.getHeartbeatHandler() == null) {
            this.heartbeatHandler = new ClientHeartbeatHandlerDefault();
        } else {
            this.heartbeatHandler = client.getHeartbeatHandler();
        }

        this.initHeartbeat();
    }

    private void initHeartbeat() {
        if (this.heartbeatScheduledFuture != null) {
            this.heartbeatScheduledFuture.cancel(false);
        }

        if (this.connector.autoReconnect()) {
            this.heartbeatScheduledFuture = RunUtils.scheduleWithFixedDelay(() -> {
                try {
                    this.heartbeatHandle();
                } catch (Throwable var2) {
                    if (log.isDebugEnabled()) {
                        log.debug("Client channel heartbeat failed: {link={}}", this.connector.getConfig().getLinkUrl());
                    }
                }

            }, this.client.getHeartbeatInterval(), this.client.getHeartbeatInterval());
        }

    }

    private void heartbeatHandle() throws Throwable {
        if (this.real != null) {
            if (this.real.getHandshake() == null) {
                return;
            }

            if (this.real.closeCode() == 2009 || this.real.closeCode() == 2008) {
                if (log.isDebugEnabled()) {
                    log.debug("Client channel is closed (pause heartbeat), sessionId={}", this.getSession().sessionId());
                }

                this.close(this.real.closeCode());
                return;
            }

            if (this.real.isClosing()) {
                return;
            }
        }

        try {
            this.internalCheck();
            this.heartbeatHandler.clientHeartbeat(this.getSession());
        } catch (YourSocketException var2) {
            throw var2;
        } catch (Throwable var3) {
            if (this.connector.autoReconnect()) {
                this.internalCloseIfError();
            }

            throw var3;
        }
    }
@Override
    public boolean isValid() {
        return this.real == null ? false : this.real.isValid();
    }
    @Override
    public boolean isClosing() {
        return this.real == null ? false : this.real.isClosing();
    }
    @Override
    public int closeCode() {
        return this.real == null ? 0 : this.real.closeCode();
    }
    @Override
    public long getLiveTime() {
        return this.real == null ? 0L : this.real.getLiveTime();
    }
@Override
    public InetSocketAddress getRemoteAddress() throws IOException {
        return this.real == null ? null : this.real.getRemoteAddress();
    }
@Override
    public InetSocketAddress getLocalAddress() throws IOException {
        return this.real == null ? null : this.real.getLocalAddress();
    }

    @Override
    public void sendAlarm(Message from, Entity alarm) throws IOException {

    }

    @Override
    public void sendPressure(Message form, Entity pressure) throws IOException {

    }

    @Override
    public void send(Frame frame, StreamInternal stream) throws IOException {

        try {
            this.internalCheck();
            if (this.real == null) {
                throw new YourSocketChannelException("Client channel is not connected");
            } else {
                this.real.send(frame, stream);
            }
        } catch (YourSocketException var4) {
            throw var4;
        } catch (Throwable var5) {
            if (this.connector.autoReconnect()) {
                this.internalCloseIfError();
            }

            throw new YourSocketChannelException("Client channel send failed", var5);
        }
    }
    @Override
    public void onError(Throwable error) {
        this.real.onError(error);
    }
    @Override
    public void close(int code) {
        RunUtils.runAndTry(() -> {
            this.heartbeatScheduledFuture.cancel(true);
        });
        RunUtils.runAndTry(() -> {
            this.connector.close();
        });
        RunUtils.runAndTry(() -> {
            this.real.close(code);
        });
        super.close(code);
    }
@Override
    public Session getSession() {
        return this.sessionShell;
    }
    @Override
    public void reconnect() throws IOException {
        this.initHeartbeat();
        this.internalCheck();
    }

    public void connect() throws IOException {
        if (this.isConnecting.compareAndSet(false, true)) {
            try {
                if (this.real != null) {
                    this.real.close(2002);
                }

                this.real = this.client.getConnectHandler().clientConnect(this.connector);
                this.real.setSession(this.sessionShell);
                this.setHandshake(this.real.getHandshake());
            } finally {
                this.isConnecting.set(false);
            }

        }
    }

    private void internalCloseIfError() {
        if (this.real != null) {
            this.real.close(2001);
            this.real = null;
        }

    }

    private boolean internalCheck() throws IOException {
        if (this.real != null && this.real.isValid()) {
            return false;
        } else {
            this.connect();
            return true;
        }
    }
}

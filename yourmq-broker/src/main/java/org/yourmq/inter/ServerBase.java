//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.yourmq.inter;

import org.yourmq.base.*;
import org.yourmq.common.Message;
import org.yourmq.utils.RunUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ServerBase<T extends ChannelAssistant> implements Server, Listener {
    protected final Processor processor = new ProcessorDefault();
    protected final Collection<Session> sessions = Collections.newSetFromMap(new ConcurrentHashMap<>());
    protected Listener listener = new SimpleListener();

    protected final ServerConfig config;
    protected final T assistant;
    protected boolean isStarted;

    public ServerBase(ServerConfig config, T assistant) {
        this.config = config;
        this.assistant = assistant;
        this.processor.setListener(this);
    }

    /**
     * 获取通道助理
     */
    public T getAssistant() {
        return assistant;
    }

    /**
     * 获取配置
     */
    @Override
    public ServerConfig getConfig() {
        return config;
    }

    /**
     * 配置
     */
    @Override
    public Server config(ServerConfigHandler configHandler) {
        if (configHandler != null) {
            configHandler.serverConfig(config);
        }
        return this;
    }


    /**
     * 获取处理器
     */
    public Processor getProcessor() {
        return processor;
    }


    /**
     * 设置监听器
     */
    @Override
    public Server listen(Listener listener) {
        if (listener != null) {
            this.listener = listener;
        }
        return this;
    }

    @Override
    public void prestop() {
        prestopDo();
    }

    @Override
    public void stop() {
        stopDo();
    }

    @Override
    public void onOpen(Session s) throws IOException {
        sessions.add(s);
        listener.onOpen(s);
    }

    @Override
    public void onMessage(Session s, Message m) throws IOException {
        listener.onMessage(s, m);
    }

    @Override
    public void onReply(Session s, Message m) {
        listener.onReply(s, m);
    }

    @Override
    public void onSend(Session s, Message m) {
        listener.onSend(s, m);
    }

    @Override
    public void onClose(Session s) {
        sessions.remove(s);
        listener.onClose(s);
    }

    @Override
    public void onError(Session s, Throwable e) {
        listener.onError(s, e);
    }

    /**
     * 执行预停止（发送 close-starting 指令）
     */
    protected void prestopDo() {
        for (Session s1 : sessions) {
            if (s1.isValid()) {
                RunUtils.runAndTry(s1::preclose);
            }
        }
    }

    /**
     * 执行预停止（发送 close 指令）
     */
    protected void stopDo() {
        for (Session s1 : sessions) {
            if (s1.isValid()) {
                RunUtils.runAndTry(s1::close);
            }
        }
        sessions.clear();
    }
}

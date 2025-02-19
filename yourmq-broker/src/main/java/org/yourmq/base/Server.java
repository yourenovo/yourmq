package org.yourmq.base;

import org.yourmq.inter.ServerConfig;

import java.io.IOException;

public interface Server {
    /**
     * 获取台头
     */
    String getTitle();

    /**
     * 获取配置
     */
    ServerConfig getConfig();

    /**
     * 配置
     */
    Server config(ServerConfigHandler configHandler);

    /**
     * 监听
     */
    Server listen(Listener listener);

    /**
     * 启动
     */
    Server start() throws IOException;

    /**
     * 预停止
     */
    void prestop();

    /**
     * 停止
     */
    void stop();
}


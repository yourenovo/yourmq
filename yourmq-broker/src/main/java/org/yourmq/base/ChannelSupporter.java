package org.yourmq.base;

public interface ChannelSupporter<S> {
    /**
     * 处理器
     */
    Processor getProcessor();

    /**
     * 配置
     */
    Config getConfig();

    /**
     * 通道助理
     */
    ChannelAssistant<S> getAssistant();
}
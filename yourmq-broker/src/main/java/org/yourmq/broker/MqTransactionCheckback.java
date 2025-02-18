package org.yourmq.broker;

import org.yourmq.base.MqMessageReceived;

public interface MqTransactionCheckback {
    /**
     * 检查
     *
     * @param message 消息
     */
    void check(MqMessageReceived message) throws Exception;
}

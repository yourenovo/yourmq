package org.yourmq.broker;

import org.yourmq.base.MqMessageReceived;

@FunctionalInterface
public interface MqConsumeHandler {
    /**
     * 消费
     *
     * @param message 收到的消息
     */
    void consume(MqMessageReceived message) throws Exception;
}

package org.yourmq.base;

import org.yourmq.client.MqMessageBase;
import org.yourmq.common.Entity;

import java.io.IOException;

/**
 * 收到的消息接口
 *
 * @author your
 * @since 1.0
 */
public interface MqMessageReceived extends MqMessageBase {
    /**
     * 主题
     */
    String getTopic();

    /**
     * 内容
     *
     * @deprecated 1.4
     */
    @Deprecated
    default String getContent() {
        return getBodyAsString();
    }

    /**
     * 数据
     */
    String getBodyAsString();

    /**
     * 消费者组
     */
    String getConsumerGroup();

    /**
     * 已派发次数
     */
    int getTimes();

    /**
     * 回执
     */
    void acknowledge(boolean isOk) throws IOException;

    /**
     * 响应
     */
    void response(Entity entity) throws IOException;
}

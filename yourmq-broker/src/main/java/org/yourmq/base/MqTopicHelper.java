package org.yourmq.base;

import org.yourmq.utils.StrUtils;

/**
 * @author your
 * @since 1.4
 */
public class MqTopicHelper {
    /**
     * 获取完整主题
     */
    public static String getFullTopic(String namespace, String topic) {
        if (StrUtils.isEmpty(namespace)) {
            return topic;
        } else {
            return namespace + ":" + topic;
        }
    }

    /**
     * 获取主题
     */
    public static String getTopic(String fullTopic) {
        int idx = fullTopic.indexOf(":");
        if (idx > 0) {
            return fullTopic.substring(idx + 1);
        } else {
            return fullTopic;
        }
    }
}

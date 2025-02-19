package org.yourmq.base;

/**
 * 消息元信息分析器 v2
 *
 * @author your
 * @since 1.2
 */
public class MqMetasResolverV3 extends MqMetasResolverV2 {
    @Override
    public int version() {
        return 3;
    }
}
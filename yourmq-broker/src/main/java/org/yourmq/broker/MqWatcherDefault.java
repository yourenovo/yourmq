package org.yourmq.broker;

import org.yourmq.base.Session;
import org.yourmq.common.Message;

/**
 * 消息观察者默认实现
 *
 * @author your
 * @since
 */
public class MqWatcherDefault implements MqWatcher {


    @Override
    public void init(MqBorkerInternal serverInternal) {

    }

    @Override
    public void onStartBefore() {

    }

    @Override
    public void onStartAfter() {

    }

    @Override
    public void onStopBefore() {

    }

    @Override
    public void onStopAfter() {

    }

    @Override
    public void onSave() {

    }

    @Override
    public void onSubscribe(String topic, String consumerGroup, Session session) {

    }

    @Override
    public void onUnSubscribe(String topic, String consumerGroup, Session session) {

    }

    @Override
    public void onPublish(Message message) {

    }

    @Override
    public void onUnPublish(Message message) {

    }

    @Override
    public void onDistribute(String topic, String consumerGroup, MqMessageHolder messageHolder) {

    }

    @Override
    public void onAcknowledge(String topic, String consumerGroup, MqMessageHolder messageHolder, boolean isOk) {

    }
}

package org.yourmq.snap;

import org.yourmq.base.MessageHandler;
import org.yourmq.base.ONode;
import org.yourmq.base.Result;
import org.yourmq.base.Session;
import org.yourmq.broker.MqBorkerListener;
import org.yourmq.broker.MqBrokerConfig;
import org.yourmq.common.Message;
import org.yourmq.common.MqConstants;
import org.yourmq.common.StringEntity;
import org.yourmq.utils.StrUtils;

import java.io.IOException;
import java.util.List;

public class YourmqApiHandler implements MessageHandler {
    private MqBorkerListener serviceListener;
    private QueueForceService queueForceService;

    public YourmqApiHandler(QueueForceService queueForceService, MqBorkerListener serviceListener) {
        this.queueForceService = queueForceService;
        this.serviceListener = serviceListener;
    }

    @Override
    public void handle(Session s, Message m) throws IOException {
        String name = m.meta(MqConstants.API_NAME);
        String token = m.meta(MqConstants.API_TOKEN);

        if (StrUtils.isEmpty(MqBrokerConfig.API_TOKEN)) {
            s.sendAlarm(m, "Api calls are not supported");
            return;
        }

        if (MqBrokerConfig.API_TOKEN.equals(token) == false) {
            s.sendAlarm(m, "Token is invalid");
            return;
        }


        String topic = m.meta(MqConstants.MQ_META_TOPIC);
        String consumerGroup = m.meta(MqConstants.MQ_META_CONSUMER_GROUP);
        String queueName = topic + MqConstants.SEPARATOR_TOPIC_CONSUMER_GROUP + consumerGroup;


        try {
            if (MqApis.MQ_QUEUE_LIST.equals(name)) {
                //{code,data:[{queue,sessionCount,messageCount,....}]}
                List<QueueVo> queueVolist = ViewUtils.queueView(serviceListener);
                replyDo(s, m, Result.succeed(queueVolist));
                return;
            }

            if (MqApis.MQ_QUEUE_VIEW_MESSAGE.equals(name)) {
                //{code,data:{queue,sessionCount,messageCount,....}}
                QueueVo queueVo = ViewUtils.queueOneView(serviceListener, queueName);
                if (queueVo == null) {
                    replyDo(s, m, Result.failure("Queue does not exist"));
                } else {
                    replyDo(s, m, Result.succeed(queueVo));
                }
                return;
            }

            if (MqApis.MQ_QUEUE_VIEW_SESSION.equals(name)) {
                //{code,data:[ip,ip]}
                List<String> list = ViewUtils.queueSessionListView(serviceListener, queueName);
                replyDo(s, m, Result.succeed(list));
                return;
            }

            if (MqApis.MQ_QUEUE_FORCE_CLEAR.equals(name)) {
                //{code,data}
                queueForceService.forceClear(serviceListener, topic, consumerGroup, MqBrokerConfig.isStandalone());
                replyDo(s, m, Result.succeed());
                return;
            }

            if (MqApis.MQ_QUEUE_FORCE_DELETE.equals(name)) {
                //{code,data}
                queueForceService.forceDelete(serviceListener, topic, consumerGroup, MqBrokerConfig.isStandalone());
                replyDo(s, m, Result.succeed());
                return;
            }

            if (MqApis.MQ_QUEUE_FORCE_DISTRIBUTE.equals(name)) {
                //{code,data}
                queueForceService.forceDelete(serviceListener, topic, consumerGroup, MqBrokerConfig.isStandalone());
                replyDo(s, m, Result.succeed());
                return;
            }
        } catch (Throwable e) {
            replyDo(s, m, Result.failure(e.getMessage()));
        }
    }

    private void replyDo(Session s, Message m, Result rst) throws IOException {
        s.replyEnd(m, new StringEntity(ONode.stringify(rst)));
    }
}

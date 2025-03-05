package org.yourmq.snap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.yourmq.YourMQ;
import org.yourmq.base.ClusterClientSession;
import org.yourmq.base.ONode;
import org.yourmq.broker.MqBorker;
import org.yourmq.broker.MqBorkerInternal;
import org.yourmq.broker.MqBorkerListener;
import org.yourmq.broker.MqBrokerConfig;
import org.yourmq.common.MqConstants;
import org.yourmq.common.StringEntity;
import org.yourmq.common.YourSocket;
import org.yourmq.utils.EventBus;
import org.yourmq.utils.MemoryUtils;
import org.yourmq.utils.Utils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component

public class YourmqLifecycle implements DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(YourmqLifecycle.class);
    private static boolean isStandalone;

    public static boolean isStandalone() {
        return isStandalone;
    }


    @Autowired
    private ApplicationContext springContext;
    @Autowired
    private QueueForceService queueForceService;

    private MqBorker localServer;

    private MqBorkerListener brokerServiceListener;
    private ClusterClientSession brokerSession;
    private MqWatcherSnapshotPlus snapshotPlus;

    @PostConstruct
    public void start() throws Throwable {
        //初始化快照持久化
        snapshotPlus = new MqWatcherSnapshotPlus();
        snapshotPlus.save900Condition(1);
        snapshotPlus.save300Condition(1);
        snapshotPlus.save100Condition(1);
        ConfigurableListableBeanFactory beanFactory = (ConfigurableListableBeanFactory) springContext.getAutowireCapableBeanFactory();

        beanFactory.registerSingleton("mqWatcherSnapshotPlus", snapshotPlus);

        if (Utils.isEmpty(MqBrokerConfig.PROXY_SERVER)) {
            isStandalone = true;
            startLocalServerMode(snapshotPlus);
        } else {
            isStandalone = false;
            startBrokerSession(MqBrokerConfig.PROXY_SERVER, snapshotPlus);
        }

        log.info("Server:main: yourmqmq-broker: Started (SOCKET.D/{}-{}, yourmqmq/{})",
                YourSocket.protocolVersion(),
                YourSocket.version(),
                YourMQ.versionName());
    }


    private void startLocalServerMode(MqWatcherSnapshotPlus snapshotPlus) throws Exception {

        //服务端（鉴权为可选。不添加则不鉴权）
        localServer = YourMQ.createBorker()
                .start(18602);

        if (MqBrokerConfig.SAVE_ENABLE) {
            localServer.watcher(snapshotPlus);
        }


        addApiEvent(localServer.getServerInternal());
        ConfigurableListableBeanFactory beanFactory = (ConfigurableListableBeanFactory) springContext.getAutowireCapableBeanFactory();

        //加入容器
        beanFactory.registerSingleton("mqBorkerInternal", localServer.getServerInternal());
        log.info("yourmqMQ local server started!");
    }

    private void startBrokerSession(String brokerServers, MqWatcherSnapshotPlus snapshotPlus) throws Exception {
        brokerServiceListener = new MqBorkerListener(true);

        //允许控制台获取队列看板
        brokerServiceListener.doOn(MqConstants.ADMIN_VIEW_QUEUE, (s, m) -> {
            if (m.isRequest() || m.isSubscribe()) {
                String json = ONode.stringify(ViewUtils.queueView(brokerServiceListener));
                s.replyEnd(m, new StringEntity(json));
            }
        });

        //允许控制台获取服务信息（暂时只看内存）
        brokerServiceListener.doOn(MqConstants.ADMIN_VIEW_INSTANCE, (s, m) -> {
            if (m.isRequest() || m.isSubscribe()) {
                ServerInfoVo infoVo = new ServerInfoVo();
                infoVo.memoryRatio = MemoryUtils.getUseMemoryRatio();
                String json = ONode.stringify(infoVo);
                s.replyEnd(m, new StringEntity(json));
            }
        });

        //允许控制台强制派发
        brokerServiceListener.doOn(MqConstants.ADMIN_QUEUE_FORCE_DISTRIBUTE, (s, m) -> {
            String topic = m.meta(MqConstants.MQ_META_TOPIC);
            String consumerGroup = m.meta(MqConstants.MQ_META_CONSUMER_GROUP);

            queueForceService.forceDistribute(brokerServiceListener, topic, consumerGroup, false);
        });

        //允许控制台强制删除
        brokerServiceListener.doOn(MqConstants.ADMIN_QUEUE_FORCE_DELETE, (s, m) -> {
            String topic = m.meta(MqConstants.MQ_META_TOPIC);
            String consumerGroup = m.meta(MqConstants.MQ_META_CONSUMER_GROUP);

            queueForceService.forceDelete(brokerServiceListener, topic, consumerGroup, false);
        });

        //允许控制台强制清空
        brokerServiceListener.doOn(MqConstants.ADMIN_QUEUE_FORCE_CLEAR, (s, m) -> {
            String topic = m.meta(MqConstants.MQ_META_TOPIC);
            String consumerGroup = m.meta(MqConstants.MQ_META_CONSUMER_GROUP);

            queueForceService.forceClear(brokerServiceListener, topic, consumerGroup, false);
        });

        addApiEvent(brokerServiceListener);

        //快照
        if (MqBrokerConfig.SAVE_ENABLE) {
            brokerServiceListener.watcher(snapshotPlus);
        }

        List<String> serverUrls = new ArrayList<>();

        //同时支持：Broker 和 Multi-Broker
        for (String url : brokerServers.split(",")) {
            url = url.trim().replace("yourmqmq://", "sd:tcp://");

            if (Utils.isEmpty(url)) {
                continue;
            }

            //确保有 @参数（外部可不加）
            if (url.contains("@=") == false) {
                if (url.contains("?")) {
                    url = url + "&@=" + MqConstants.PROXY_AT_BROKER;
                } else {
                    url = url + "?@=" + MqConstants.PROXY_AT_BROKER;
                }
            }

            serverUrls.add(url);
        }

        brokerSession = (ClusterClientSession) YourSocket.createClusterClient(serverUrls)
                .config(c -> {
                    HttpServerProps serverProps = HttpServerProps.getInstance();

                    c.metaPut(MqConstants.YOURMQ_VERSION, YourMQ.versionCodeAsString());
                    //添加主端口 - port
                    c.metaPut("port", String.valueOf(serverProps.getWrapPort()));
                    //添加主机 - host
                    c.metaPut("host", serverProps.getWrapHost());

                    c.heartbeatInterval(6_000)
                            .serialSend(true)
                            .maxMemoryRatio(0.8F)
                            .ioThreads(MqBrokerConfig.IO_THREADS)
                            .codecThreads(MqBrokerConfig.CODEC_THREADS)
                            .exchangeThreads(MqBrokerConfig.EXCHANGE_THREADS);
                    EventBus.publish(c);
                })
                .listen(brokerServiceListener)
                .open();


        //启动时恢复快照
        brokerServiceListener.start(null);
        ConfigurableListableBeanFactory beanFactory = (ConfigurableListableBeanFactory) springContext.getAutowireCapableBeanFactory();

        //加入容器
        beanFactory.registerSingleton("mqBorkerListener", brokerServiceListener);

        log.info("yourmqMQ broker service started!");
    }



    private void addApiEvent(MqBorkerInternal serviceInternal) {
        YourmqApiHandler handler = new YourmqApiHandler(queueForceService, (MqBorkerListener) serviceInternal);
        serviceInternal.doOnEvent(MqConstants.MQ_API, handler);
    }

    @Override
    public void destroy() throws Exception {
        if (localServer != null) {
            localServer.prestop();
        }

        if (brokerSession != null) {
            brokerSession.preclose();
        }
        if (localServer != null) {
            //停止时会触发快照
            localServer.stop();
        }

        if (brokerSession != null) {
            brokerSession.close();
            //停止时会触发快照
            brokerServiceListener.stop(null);
        }
    }
}
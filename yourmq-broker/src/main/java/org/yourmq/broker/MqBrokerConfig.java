package org.yourmq.broker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.yourmq.snap.YourmqLifecycle;

import java.util.Collections;
import java.util.Map;


@Configuration
@PropertySource("classpath:application.properties") // 假设配置文件名为 application.properties
public class MqBrokerConfig {

    public static boolean isStandalone() {
        // 这里假设 yourmqLifecycleBean 可以独立处理，若依赖 Spring 也需要做相应转换
        return YourmqLifecycle.isStandalone();
    }

    @Value("${yourmq.path}")
    public static final String PATH = null;

    @Value("${yourmq.displayPath}")
    public static final String DISPLAY_PATH = null;

    @Value("${yourmq.accessAk}")
    public static final String ACCESS_AK = null;

    @Value("${yourmq.accessSk}")
    public static final String ACCESS_SK = null;

    @Value("${yourmq.apiToken:}")
    public static final String API_TOKEN = null;

    @Value("${yourmq.ioThreads:1}")
    public static final int IO_THREADS = 1;

    @Value("${yourmq.codecThreads:1}")
    public static final int CODEC_THREADS = 1;

    @Value("${yourmq.exchangeThreads:1}")
    public static final int EXCHANGE_THREADS = 1;

    @Value("${yourmq.streamTimeout:${org.noear.yourmq.common.MqConstants.SERVER_STREAM_TIMEOUT_DEFAULT}}")
    public static final long STREAM_TIMEOUT = 0;

    @Value("${yourmq.saveEnable:true}")
    public static final boolean SAVE_ENABLE = true;

    @Value("${yourmq.save900:0}")
    public static final long SAVE_900 = 0;

    @Value("${yourmq.save300:0}")
    public static final long SAVE_300 = 0;

    @Value("${yourmq.save100:0}")
    public static final long SAVE_100 = 0;

    @Value("${yourmq.proxyServer:${yourmq.broker}}")
    public static final String PROXY_SERVER = null;

    @Value("${yourmq.yourmqTransportPort:0}")
    public static final int YOURMQ_TRANSPORT_PORT = 0;

    // 这里没有完全替代静态块中的逻辑，因为 Spring 不允许静态块直接使用 @Value 注解
    // 可以通过实现 InitializingBean 接口来处理复杂逻辑
    // 或者使用 @PostConstruct 注解的方法

    public static Map<String, String> getAccessMap() {
        // 这里假设配置文件中有 yourmq.access.x 配置项
        // 可以通过 @ConfigurationProperties 注解来处理 Map 类型的配置
        // 这里简单模拟获取配置
        Map<String, String> accessMap = Collections.emptyMap();
        accessMap.remove("ak");
        accessMap.remove("sk");

        String ak = ACCESS_AK;
        String sk = ACCESS_SK;

        if (ak != null && !ak.isEmpty()) {
            accessMap.put(ak, sk);
        }

        return Collections.unmodifiableMap(accessMap);
    }
}

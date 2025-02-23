package org.yourmq.broker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.yourmq.snap.YourmqLifecycle;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


@Configuration
@PropertySource("classpath:application.properties") // 假设配置文件名为 application.properties
public class MqBrokerConfig {

    public static boolean isStandalone() {
        return YourmqLifecycle.isStandalone();
    }

    public static String PATH;
    public static String DISPLAY_PATH;
    public static String ACCESS_AK;
    public static String ACCESS_SK;
    public static String API_TOKEN;
    public static int IO_THREADS;
    public static int CODEC_THREADS;
    public static int EXCHANGE_THREADS;
    public static long STREAM_TIMEOUT;
    public static boolean SAVE_ENABLE;
    public static long SAVE_900;
    public static long SAVE_300;
    public static long SAVE_100;
    public static String PROXY_SERVER;
    public static int YOURMQ_TRANSPORT_PORT;


    @Value("${yourmq.path}")
    public final String path = null;

    @Value("${yourmq.displaypath}")
    public final String displaypath = null;

    @Value("${yourmq.accessak}")
    public final String accessak = null;

    @Value("${yourmq.accesssk}")
    public final String accesssk = null;

    @Value("${yourmq.apitoken:}")
    public final String apitoken = null;

    @Value("${yourmq.iothreads:1}")
    public final int iothreads = 1;

    @Value("${yourmq.codecthreads:1}")
    public final int codecthreads = 1;

    @Value("${yourmq.exchangethreads:1}")
    public final int exchangethreads = 1;

    @Value("${yourmq.streamtimeout:60 * 1000 * 5}")
    public final long streamtimeout = 0;

    @Value("${yourmq.saveenable:true}")
    public final boolean saveenable = true;

    @Value("${yourmq.save900:0}")
    public final long save900 = 0;

    @Value("${yourmq.save300:0}")
    public final long save300 = 0;

    @Value("${yourmq.save100:0}")
    public final long save100 = 0;

    //    @Value("${yourmq.proxyserver:yourmq://127.0.0.1:10080}")
    public final String proxyserver = null;

    @Value("${yourmq.yourmqtransportport:0}")
    public final int yourmqtransportport = 0;


    // 这里没有完全替代静态块中的逻辑，因为 Spring 不允许静态块直接使用 @Value 注解
    // 可以通过实现 InitializingBean 接口来处理复杂逻辑
    // 或者使用 @PostConstruct 注解的方法
    @PostConstruct
    public void init() {
        PATH = path;
        DISPLAY_PATH = displaypath;
        ACCESS_AK = accessak;
        ACCESS_SK = accesssk;
        API_TOKEN = apitoken;
        IO_THREADS = iothreads;
        CODEC_THREADS = codecthreads;
        EXCHANGE_THREADS = exchangethreads;
        STREAM_TIMEOUT = streamtimeout;
        SAVE_ENABLE = saveenable;
        SAVE_900 = save900;
        SAVE_300 = save300;
        SAVE_100 = save100;
        PROXY_SERVER = proxyserver;
        YOURMQ_TRANSPORT_PORT = yourmqtransportport;
    }

    public static Map<String, String> getAccessMap() {
        // 这里假设配置文件中有 yourmq.access.x 配置项
        // 可以通过 @ConfigurationProperties 注解来处理 Map 类型的配置
        // 这里简单模拟获取配置
        Map<String, String> accessMap = new HashMap<>();
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
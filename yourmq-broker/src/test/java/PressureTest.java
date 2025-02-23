import org.yourmq.YourMQ;
import org.yourmq.broker.MqBorker;
import org.yourmq.client.MqClient;
import org.yourmq.client.MqMessage;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PressureTest {
    private static final int THREAD_COUNT = 100; // 线程数量，可根据需要调整
    private static final int MESSAGE_COUNT = 10000; // 每个线程发送的消息数量，可根据需要调整

    public static void main(String[] args) throws Exception {
        // 启动服务端
        MqBorker server = YourMQ.createBorker().start(18602);

        // 创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);

        // 启动多个线程进行压力测试
        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.submit(() -> {
                try {
                    // 客户端
                    MqClient client = YourMQ.createClient("YourMQ://127.0.0.1:18602?ak=ak1&sk=sk1")
                            .nameAs("demoapp")
                            .connect();

                    // 订阅消息
                    client.subscribe("demo", "a", (msg) -> {
                        System.out.println(msg.getBodyAsString());
                    });

                    // 发布消息
                    for (int j = 0; j < MESSAGE_COUNT; j++) {
                        client.publish("demo", new MqMessage("demo" + j)
                                .expiration(new Date(System.currentTimeMillis() + 5000)));
                    }

                    // 等待一段时间，确保消息处理完成
                    Thread.sleep(10000);

//                    // 关闭客户端
//                    client.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        // 关闭线程池
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        // 关闭服务端
        server.stop();
    }
}

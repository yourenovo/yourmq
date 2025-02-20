import org.yourmq.YourMQ;
import org.yourmq.broker.MqBorker;
import org.yourmq.broker.MqQueue;
import org.yourmq.client.MqClient;
import org.yourmq.client.MqMessage;

import java.util.Date;

public class test_01 {
    public static void main(String[] args) throws Exception {

        //服务端
        MqBorker start = YourMQ.createBorker()
                .start(18602);


        //客户端
        MqClient client = YourMQ.createClient("YourMQ://127.0.0.1:18602?ak=ak1&sk=sk1")
                .nameAs("demoapp")
                .connect();

        client.subscribe("demo", "a", (msg) -> {
            System.out.println(msg.getBodyAsString());
        });

        client.publish("demo", new MqMessage("demo1")
                .expiration(new Date(System.currentTimeMillis() + 5000)));

        MqQueue queue = start.getServerInternal().getQueue("demo#a");

        System.out.println(queue.messageTotal());
        assert queue.messageTotal() == 1L;

        Thread.sleep(3000);

        System.out.println(queue.messageTotal());
        assert queue.messageTotal() == 1L;

        Thread.sleep(3000);

        System.out.println(queue.messageTotal());
        assert queue.messageTotal() == 0L;
    }

}

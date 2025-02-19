import org.yourmq.YourMQ;
import org.yourmq.broker.MqBorker;
import org.yourmq.client.MqClient;
import org.yourmq.client.MqMessage;

public class test_01 {
    public static void main(String[] args) throws Exception {

        //服务端
        MqBorker start = YourMQ.createBorker()
                .start(18602);


        //客户端
        MqClient client = YourMQ.createClient("YourMQ://127.0.0.1:18602?ak=ak1&sk=sk1",
                        "YourMQ://127.0.0.1:18702?ak=ak1&sk=sk1")
                .nameAs("demoapp")
                .connect();

        client.subscribe("demo", (msg) -> {
            System.out.println(msg.getBodyAsString());
        });

        long i = 0;
        while (true) {
            client.publishAsync("demo", new MqMessage("test-" + (i++)));
        }
    }

}

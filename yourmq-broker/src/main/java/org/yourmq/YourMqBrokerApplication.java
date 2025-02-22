package org.yourmq;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@SpringBootApplication
public class YourMqBrokerApplication {

    public static void main(String[] args) {
        SpringApplication.run(YourMqBrokerApplication.class, args);
    }

    @Bean
    public CommandLineRunner onAppPluginLoadEnd() {
        return args -> {
            // 模拟 app.get("/", ctx -> ctx.redirect(MqBrokerConfig.path + "/login"));
            System.out.println("AppPluginLoadEndEvent handled: Redirect configuration set.");
        };
    }

    @RestController
    class RedirectController {

        @GetMapping("/")
        public RedirectView redirectToLogin() {
            // 假设 MqBrokerConfig.path 是一个常量或配置项
            String path = "your_mq_broker_path";
            return new RedirectView(path + "/login");
        }
    }

}


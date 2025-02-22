package org.yourmq.snap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SocketdConfig {

    // 模拟 ServerConfig 事件处理
    @Bean
    public Object handleServerConfig() {
        // 这里可以添加 ServerConfig 相关的配置逻辑
        System.out.println("ServerConfig handled.");
        return new Object();
    }

    // 模拟 ClientConfig 事件处理
    @Bean
    public Object handleClientConfig() {
        // 这里可以添加 ClientConfig 相关的配置逻辑
        System.out.println("ClientConfig handled.");
        return new Object();
    }
}

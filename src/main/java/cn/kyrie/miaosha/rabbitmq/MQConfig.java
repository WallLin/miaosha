package cn.kyrie.miaosha.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author kyrie
 * @date 2020-01-02 - 15:32
 */
@Configuration
public class MQConfig {

    public static final String QUEUE = "queue";

    /**
     * Direct模式 交换机Exchange
     */
    @Bean
    public Queue queue() {
        return new Queue(QUEUE, true); // arg1: 队列名称; arg2: 是否持久化
    }
}

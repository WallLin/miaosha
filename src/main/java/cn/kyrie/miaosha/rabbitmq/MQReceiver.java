package cn.kyrie.miaosha.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * @author kyrie
 * @date 2020-01-02 - 15:39
 */
@Service
public class MQReceiver {

    private Logger log = LoggerFactory.getLogger(MQReceiver.class);

    /**
     * Direct模式 交换机Exchange
     * @param message
     */
    @RabbitListener(queues = MQConfig.QUEUE)
    public void receive(String message) {
        log.info("receive: " + message);
    }

    /**
     * Topic模式 交换机Exchange
     * @param message
     */
    /*@RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
    public void receiveTopic1(String message) {
        log.info("topic queue1 receive: " + message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
    public void receiveTopic2(String message) {
        log.info("topic queue2 receive: " + message);
    }*/

    /**
     * Fanout模式 交换机Exchange
     * @param message
     */
    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
    public void receiveTopic1(String message) {
        log.info("fanout queue1 receive: " + message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
    public void receiveTopic2(String message) {
        log.info("fanout queue2 receive: " + message);
    }
}

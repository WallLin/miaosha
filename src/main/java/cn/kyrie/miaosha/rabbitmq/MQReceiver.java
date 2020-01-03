package cn.kyrie.miaosha.rabbitmq;

import cn.kyrie.miaosha.domain.MiaoshaOrder;
import cn.kyrie.miaosha.domain.MiaoshaUser;
import cn.kyrie.miaosha.domain.OrderInfo;
import cn.kyrie.miaosha.redis.RedisService;
import cn.kyrie.miaosha.result.CodeMsg;
import cn.kyrie.miaosha.result.Result;
import cn.kyrie.miaosha.service.GoodsService;
import cn.kyrie.miaosha.service.MiaoshaService;
import cn.kyrie.miaosha.service.OrderService;
import cn.kyrie.miaosha.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author kyrie
 * @date 2020-01-02 - 15:39
 */
@Service
public class MQReceiver {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    private Logger log = LoggerFactory.getLogger(MQReceiver.class);

    @RabbitListener(queues = MQConfig.MIAOSHA_QUEUE)
    public void receive(String message) {
        if (message == null) {
            return;
        }
        log.info("receive: " + message);
        MiaoshaMessage mm = RedisService.stringToBean(message, MiaoshaMessage.class);
        MiaoshaUser user = mm.getMiaoshaUser();
        long goodsId = mm.getGoodsId();
        // 查数据库
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if (stock <= 0) {
            return;
        }
        // 判断该用户是否已经秒杀了，避免重复秒杀
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdAndGoodsId(user.getId(), goodsId);
        if (miaoshaOrder != null) {
            return;
        }
        // 减库存、下订单、写入秒杀订单
        OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
    }

    /**
     * Direct模式 交换机Exchange
     * @param message
     *//*
    @RabbitListener(queues = MQConfig.QUEUE)
    public void receive(String message) {
        log.info("receive: " + message);
    }*/

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
    /*@RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
    public void receiveTopic1(String message) {
        log.info("fanout queue1 receive: " + message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
    public void receiveTopic2(String message) {
        log.info("fanout queue2 receive: " + message);
    }*/
}

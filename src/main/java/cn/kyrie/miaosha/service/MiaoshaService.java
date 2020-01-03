package cn.kyrie.miaosha.service;

import cn.kyrie.miaosha.domain.MiaoshaOrder;
import cn.kyrie.miaosha.domain.MiaoshaUser;
import cn.kyrie.miaosha.domain.OrderInfo;
import cn.kyrie.miaosha.redis.MiaoshaKey;
import cn.kyrie.miaosha.redis.RedisService;
import cn.kyrie.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author kyrie
 * @date 2019-12-22 - 20:54
 */
@Service
public class MiaoshaService {
    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    /**
     * 秒杀: 减库存、下订单、写入秒杀订单
     * @param user
     * @param goods
     * @return
     */
    @Transactional
    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {
        // 减库存
        boolean success = goodsService.reduceStock(goods.getId());
        if (success) {
            // 下订单、写入秒杀订单(order_info miaosha_order)
            return orderService.createOrder(user, goods);
        } else {
            setGoodsOver(goods.getId()); // 添加标记，商品已卖完
            return null;
        }
    }

    private void setGoodsOver(Long goodsId) {
        redisService.set(MiaoshaKey.isGoodsOver, "" + goodsId, true);
    }

    /**
     *
     * @param userId
     * @param goodsId
     * @return orderId: 秒杀成功; -1: 秒杀失败; 0: 排队中
     */
    public long getMiaoshaResult(long userId, long goodsId) {
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdAndGoodsId(userId, goodsId);
        if (order != null) { // 秒杀成功
            return order.getOrderId();
        } else {
            boolean isOver = getGoodsOver(goodsId);
            if (isOver) {
                return -1;
            } else {
                return 0; // 继续轮询
            }
        }
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(MiaoshaKey.isGoodsOver, "" + goodsId);
    }
}

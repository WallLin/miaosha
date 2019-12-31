package cn.kyrie.miaosha.service;

import cn.kyrie.miaosha.dao.OrderDao;
import cn.kyrie.miaosha.domain.MiaoshaOrder;
import cn.kyrie.miaosha.domain.MiaoshaUser;
import cn.kyrie.miaosha.domain.OrderInfo;
import cn.kyrie.miaosha.redis.OrderKey;
import cn.kyrie.miaosha.redis.RedisService;
import cn.kyrie.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author kyrie
 * @date 2019-12-22 - 20:54
 */
@Service
public class OrderService {
    @Autowired
    OrderDao orderDao;

    @Autowired
    GoodsService goodsService;

    @Autowired
    RedisService redisService;

    public MiaoshaOrder getMiaoshaOrderByUserIdAndGoodsId(long userId, long goodsId) {
        //return orderDao.getByUserIdAndGoodsId(userId, goodsId);
        // 从缓存中取
        return redisService.get(OrderKey.getMiaoshaOrderByUidAndGid, "" + userId + "_" + goodsId, MiaoshaOrder.class);
    }

    /**
     * 下订单、写入秒杀订单
     * @param user
     * @param goods
     * @return
     */
    @Transactional
    public OrderInfo createOrder(MiaoshaUser user, GoodsVo goods) {
        // 下订单
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setUserId(user.getId());
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setCreateDate(new Date());
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsPrice(goods.getMiaoshaPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        long orderId = orderDao.insert(orderInfo);  // 返回订单id
        // 写入秒杀订单
        MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setUserId(user.getId());   // 加上唯一索引
        miaoshaOrder.setGoodsId(goods.getId()); // 加上唯一索引
        miaoshaOrder.setOrderId(orderId);
        orderDao.insertMiaoshaOrder(miaoshaOrder);

        // 将订单放到缓存中
        redisService.set(OrderKey.getMiaoshaOrderByUidAndGid, "" + user.getId() + "_" + goods.getId(), miaoshaOrder);
        return orderInfo;
    }

    public OrderInfo getOrderById(long orderId) {
        return orderDao.getOrderById(orderId);
    }
}

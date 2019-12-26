package cn.kyrie.miaosha.service;

import cn.kyrie.miaosha.domain.MiaoshaUser;
import cn.kyrie.miaosha.domain.OrderInfo;
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

    /**
     * 秒杀: 减库存、下订单、写入秒杀订单
     * @param user
     * @param goods
     * @return
     */
    @Transactional
    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {
        // 减库存
        goodsService.reduceStock(goods.getId());

        // 下订单、写入秒杀订单(order_info miaosha_order)
        return orderService.createOrder(user, goods);
    }
}

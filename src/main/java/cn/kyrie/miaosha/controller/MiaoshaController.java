package cn.kyrie.miaosha.controller;

import cn.kyrie.miaosha.domain.MiaoshaUser;
import cn.kyrie.miaosha.rabbitmq.MQSender;
import cn.kyrie.miaosha.rabbitmq.MiaoshaMessage;
import cn.kyrie.miaosha.redis.GoodsKey;
import cn.kyrie.miaosha.redis.RedisService;
import cn.kyrie.miaosha.result.CodeMsg;
import cn.kyrie.miaosha.result.Result;
import cn.kyrie.miaosha.service.GoodsService;
import cn.kyrie.miaosha.service.MiaoshaService;
import cn.kyrie.miaosha.service.OrderService;
import cn.kyrie.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 秒杀控制器
 *
 * @author kyrie
 * @date 2019-12-22 - 10:52
 */
@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender sender;

    // 内存标记，减少对redis访问
    private Map<Long, Boolean> localOverMap = new HashMap<>();

    /**
     * 系统初始化
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        if (goodsList == null) {
            return;
        }
        // 将结果设置到缓存中
        for (GoodsVo goods : goodsList) {
            redisService.set(GoodsKey.getMiaoshaGoodsStock, "" + goods.getId(), goods.getStockCount());
            localOverMap.put(goods.getId(), false); // 初始化，有库存
        }
    }

    /**
     * 获取秒杀地址
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaPath(MiaoshaUser user, @RequestParam(name = "goodsId") long goodsId) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        String path = miaoshaService.createMiaoshaPath(user, goodsId);
        return Result.success(path);
    }

    /**
     * 2000 * 10
     *
     * error: 1)4.52%; 2)3.75%; 3)5.78%;
     *
     * QPS:  1)921; 2)1145; 3)991;
     *
     * 优化
     * error:0.01%
     * QPS:2071
     *
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/{path}/do_miaosha", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> doMiaosha(MiaoshaUser user, @RequestParam(name = "goodsId") long goodsId,
                                     @PathVariable(name = "path") String path) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        // 检查秒杀地址是否正确
        boolean check = miaoshaService.checkMiaoshaPath(user, goodsId, path);
        if (!check) {
            return Result.error(CodeMsg.IILEGAL_PATH);
        }

        // 内存标记，减少redis访问, 当第11，12个请求过来的时候，已经没有库存了，没必要访问redis了，直接返回秒杀失败
        boolean over = localOverMap.get(goodsId);
        if (over) {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        // 如果有库存，才会进行下面的操作
        // 预减库存
        long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, "" + goodsId);
        if (stock < 0) {
            localOverMap.put(goodsId, true); // 没有库存
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        // 入队
        MiaoshaMessage mm = new MiaoshaMessage();
        mm.setMiaoshaUser(user);
        mm.setGoodsId(goodsId);
        sender.sendMiaoshaMessage(mm);
        return Result.success(0); // 排队中

        /*// 判断库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        // 10个商品，一个用户同时发送两个请求：req1、req2, 解决方法：给数据库表字段(userId,goodsId)加上唯一索引
        int stockCount = goods.getStockCount();
        if (stockCount <= 0) {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        // 判断该用户是否已经秒杀了，避免重复秒杀
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdAndGoodsId(user.getId(), goodsId);
        if (miaoshaOrder != null) {
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        // 减库存、下订单、写入秒杀订单
        OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
        return Result.success(orderInfo);*/
    }

    /**
     *
     * @param user
     * @param goodsId
     * @return orderId: 秒杀成功; -1: 秒杀失败; 0: 排队中
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> miaoshaResult(MiaoshaUser user, @RequestParam(name = "goodsId") long goodsId) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        long res = miaoshaService.getMiaoshaResult(user.getId(), goodsId);
        return Result.success(res);
    }
}

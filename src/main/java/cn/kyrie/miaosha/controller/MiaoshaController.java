package cn.kyrie.miaosha.controller;

import cn.kyrie.miaosha.domain.MiaoshaOrder;
import cn.kyrie.miaosha.domain.MiaoshaUser;
import cn.kyrie.miaosha.domain.OrderInfo;
import cn.kyrie.miaosha.exception.GlobalException;
import cn.kyrie.miaosha.result.CodeMsg;
import cn.kyrie.miaosha.result.Result;
import cn.kyrie.miaosha.service.GoodsService;
import cn.kyrie.miaosha.service.MiaoshaService;
import cn.kyrie.miaosha.service.OrderService;
import cn.kyrie.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 秒杀控制器
 *
 * @author kyrie
 * @date 2019-12-22 - 10:52
 */
@Controller
@RequestMapping("/miaosha")
public class MiaoshaController {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    /**
     * 2000 * 10
     *
     * error: 1)4.52%; 2)3.75%; 3)5.78%;
     *
     * QPS:  1)921; 2)1145; 3)991;
     *
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping("/do_miaosha")
    @ResponseBody
    public Result<OrderInfo> doMiaosha(MiaoshaUser user, @RequestParam(name = "goodsId") long goodsId) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        // 判断库存
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
        return Result.success(orderInfo);
    }




    /*@RequestMapping("/do_miaosha")
    public String doMiaosha(Model model, MiaoshaUser user,
                            @RequestParam(name = "goodsId") long goodsId) {
        if (user == null) {
            return "login";
        }
        // 判断库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stockCount = goods.getStockCount();
        if (stockCount <= 0) {
            model.addAttribute("errmsg", CodeMsg.MIAO_SHA_OVER);
            return "miaosha_fail";
        }
        // 判断该用户是否已经秒杀了，避免重复秒杀
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdAndGoodsId(user.getId(), goodsId);
        if (miaoshaOrder != null) {
            model.addAttribute("errmsg", CodeMsg.REPEATE_MIAOSHA);
            return "miaosha_fail";
        }
        // 减库存、下订单、写入秒杀订单
        OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
        model.addAttribute("orderInfo", orderInfo);
        model.addAttribute("goods", goods);
        return "order_detail";
    }*/
}

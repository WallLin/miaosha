package cn.kyrie.miaosha.controller;

import cn.kyrie.miaosha.domain.MiaoshaOrder;
import cn.kyrie.miaosha.domain.MiaoshaUser;
import cn.kyrie.miaosha.domain.OrderInfo;
import cn.kyrie.miaosha.result.CodeMsg;
import cn.kyrie.miaosha.service.GoodsService;
import cn.kyrie.miaosha.service.MiaoshaService;
import cn.kyrie.miaosha.service.OrderService;
import cn.kyrie.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
     * error: 1)0.78%; 2)1.52%; 3)0.90%;
     *
     * QPS:  1)835; 2)1016; 3)1140;
     *
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping("/do_miaosha")
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
    }
}

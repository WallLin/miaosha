package cn.kyrie.miaosha.controller;

import cn.kyrie.miaosha.domain.MiaoshaUser;
import cn.kyrie.miaosha.redis.GoodsKey;
import cn.kyrie.miaosha.redis.RedisService;
import cn.kyrie.miaosha.service.GoodsService;
import cn.kyrie.miaosha.vo.GoodsVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import java.util.List;

/**
 * 商品控制器
 *
 * @author kyrie
 * @date 2019-12-22 - 10:52
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    GoodsService goodsService;

    @Autowired
    RedisService redisService;

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver; // 手动渲染模板

    /**
     * Jmeter压测
     *
     * 2000 * 10
     *
     * 1.没有页面缓存
     * error: 1)3.46%; 2)5.91%; 3)8.42%
     *
     * QPS: 1)450; 2)409; 3)462
     *
     * @param model
     * @param user
     * @return
     */
    @RequestMapping(value = "/to_list", produces = "text/html")
    @ResponseBody
    public String list(HttpServletRequest request, HttpServletResponse response,
                       Model model, MiaoshaUser user) {
        model.addAttribute("user", user);
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        model.addAttribute("goodsList", goodsList);
        //return "goods_list";

        // 1、取缓存
        String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
        if (!StringUtils.isEmpty(html)) {
            return html;
        }
        // spring5
        WebContext ctx = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        // 2、手动渲染模板
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", ctx);
        if (!StringUtils.isEmpty(html)) {
            // 将页面放到缓存中
            redisService.set(GoodsKey.getGoodsList, "", html);
        }
        return html;
    }

    /**
     * URL缓存
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/to_detail/{goodsId}", produces = "text/html")
    @ResponseBody
    public String detail(HttpServletRequest request, HttpServletResponse response,
                         Model model, MiaoshaUser user,
                         @PathVariable(name = "goodsId") Long goodsId) {
        model.addAttribute("user", user);
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);

        // 1、取缓存
        // 与页面缓存不一样的地方是：每个商品对应一个详情页面，因此每个详情页要单独存储，设置key时要加上商品的id
        String html = redisService.get(GoodsKey.getGoodsDetail, "" + goodsId, String.class);
        if (!StringUtils.isEmpty(html)) {
            return html;
        }

        long startAt = goods.getStartDate().getTime(); // 秒杀开始时间
        long endAt = goods.getEndDate().getTime(); // 秒杀结束时间
        long now = System.currentTimeMillis(); // 当前时间

        int miaoshaStatus = 0; // '0':秒杀倒计时；'1':秒杀进行中；'2':秒杀已结束
        int remainSeconds = 0; // 秒杀倒计时还剩多少秒

        if (now < startAt) { // 秒杀还没开始，倒计时
            miaoshaStatus = 0;
            remainSeconds = (int) ((startAt - now) / 1000);
        } else if (now > endAt) { // 秒杀已结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        } else { // 秒杀进行中
            miaoshaStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("goods", goods);
        model.addAttribute("miaoshaStatus", miaoshaStatus);
        model.addAttribute("remainSeconds", remainSeconds);
        // return "goods_detail";
        // spring5
        WebContext ctx = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        // 2、手动渲染模板
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", ctx);
        if (!StringUtils.isEmpty(html)) {
            // 将页面放到缓存中
            redisService.set(GoodsKey.getGoodsDetail, "" + goodsId, html);
        }
        return html;
    }
}

package cn.kyrie.miaosha.service;

import cn.kyrie.miaosha.domain.MiaoshaOrder;
import cn.kyrie.miaosha.domain.MiaoshaUser;
import cn.kyrie.miaosha.domain.OrderInfo;
import cn.kyrie.miaosha.redis.MiaoshaKey;
import cn.kyrie.miaosha.redis.RedisService;
import cn.kyrie.miaosha.util.MD5Util;
import cn.kyrie.miaosha.util.UUIDUtil;
import cn.kyrie.miaosha.vo.GoodsVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

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
     *
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
                return -1; // 秒杀失败
            } else {
                return 0; // 继续轮询
            }
        }
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(MiaoshaKey.isGoodsOver, "" + goodsId);
    }

    /**
     * 检查秒杀地址是否正确
     *
     * @param user
     * @param goodsId
     * @param path
     * @return
     */
    public boolean checkMiaoshaPath(MiaoshaUser user, long goodsId, String path) {
        if (user == null || StringUtils.isEmpty(path)) {
            return false;
        }
        String oldPath = redisService.get(MiaoshaKey.getMiaoshaPath, "" + user.getId() + "_" + goodsId, String.class);
        return path.equals(oldPath);
    }

    /**
     * 创建秒杀地址
     *
     * @param user
     * @param goodsId
     * @return
     */
    public String createMiaoshaPath(MiaoshaUser user, long goodsId) {
        if (user == null || goodsId <= 0) {
            return null;
        }
        String path = MD5Util.md5(UUIDUtil.uuid() + "123456");
        // 保存到redis中
        redisService.set(MiaoshaKey.getMiaoshaPath, "" + user.getId() + "_" + goodsId, path);
        return path;
    }

    /**
     * 生成验证码
     *
     * @param user
     * @param goodsId
     * @return
     */
    public BufferedImage createVerifyCode(MiaoshaUser user, long goodsId) {
        if (user == null || goodsId <= 0) {
            return null;
        }
        int width = 100;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码存到redis中
        int rnd = calc(verifyCode);
        redisService.set(MiaoshaKey.getMiaoshaVerifyCode, user.getId() + "," + goodsId, rnd);
        //输出图片
        return image;
    }

    /**
     * 计算公式，返回结果
     * ScriptEngine使用
     *
     * @param verifyCode
     * @return
     */
    private static int calc(String verifyCode) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (Integer) engine.eval(verifyCode);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * + - *
     */
    private static char[] ops = new char[]{'+', '-', '*'};

    private String generateVerifyCode(Random rdm) {
        // 随机生成3个(0~9)的数
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        String verifyCode = "" + num1 + op1 + num2 + op2 + num3;
        return verifyCode;
    }

    /**
     * 校验验证码
     *
     * @param user
     * @param goodsId
     * @param verifyCode
     * @return
     */
    public boolean checkVerifyCode(MiaoshaUser user, long goodsId, int verifyCode) {
        if (user == null || goodsId <= 0) {
            return false;
        }
        Integer oldVerifyCode = redisService.get(MiaoshaKey.getMiaoshaVerifyCode, user.getId() + "," + goodsId, Integer.class);
        if (oldVerifyCode == null || (oldVerifyCode - verifyCode) != 0) {
            return false;
        }
        redisService.delete(MiaoshaKey.getMiaoshaVerifyCode, user.getId() + "," + goodsId);
        return true;
    }
}

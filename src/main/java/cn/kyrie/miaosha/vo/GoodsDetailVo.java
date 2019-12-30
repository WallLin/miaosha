package cn.kyrie.miaosha.vo;

import cn.kyrie.miaosha.domain.MiaoshaUser;
import lombok.Data;

/**
 * @author kyrie
 * @date 2019-12-30 - 19:29
 */
@Data
public class GoodsDetailVo {
    private GoodsVo goods;
    private MiaoshaUser user;
    int miaoshaStatus = 0; // '0':秒杀倒计时；'1':秒杀进行中；'2':秒杀已结束
    int remainSeconds = 0; // 秒杀倒计时还剩多少秒
}

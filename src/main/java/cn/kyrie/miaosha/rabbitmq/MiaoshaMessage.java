package cn.kyrie.miaosha.rabbitmq;

import cn.kyrie.miaosha.domain.MiaoshaUser;
import lombok.Data;

/**
 * @author kyrie
 * @date 2020-01-02 - 22:48
 */
@Data
public class MiaoshaMessage {
    private MiaoshaUser miaoshaUser;
    private long goodsId;
}

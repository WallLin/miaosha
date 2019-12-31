package cn.kyrie.miaosha.vo;

import cn.kyrie.miaosha.domain.OrderInfo;
import lombok.Data;

/**
 * @author kyrie
 * @date 2019-12-30 - 19:29
 */
@Data
public class OrderDetailVo {
    private GoodsVo goods;
    private OrderInfo order;
}

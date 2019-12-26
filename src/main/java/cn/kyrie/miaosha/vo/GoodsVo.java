package cn.kyrie.miaosha.vo;

import cn.kyrie.miaosha.domain.Goods;
import lombok.Data;

import java.util.Date;

/**
 * @author kyrie
 * @date 2019-12-26 - 10:29
 */
@Data
public class GoodsVo extends Goods {
    private double miaoshaPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;
}

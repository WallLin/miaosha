package cn.kyrie.miaosha.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author kyrie
 * @date 2019-12-26 - 10:24
 */
@Data
public class MiaoshaGoods {
    private Long id;
    private Long goodsId;
    private double miaoshaPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;
}

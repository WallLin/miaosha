package cn.kyrie.miaosha.domain;

import lombok.Data;

/**
 * @author kyrie
 * @date 2019-12-26 - 10:22
 */
@Data
public class Goods {
    private Long id;
    private String goodsName;
    private String goodsTitle;
    private String goodsImg;
    private String goodsDetail;
    private Double goodsPrice;
    private Integer goodsStock;
}

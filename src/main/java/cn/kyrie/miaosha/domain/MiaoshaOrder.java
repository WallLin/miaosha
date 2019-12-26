package cn.kyrie.miaosha.domain;

import lombok.*;

@Data
public class MiaoshaOrder {
	private Long id;
	private Long userId;
	private Long  orderId;
	private Long goodsId;
}

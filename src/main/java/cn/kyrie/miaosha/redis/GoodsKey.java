package cn.kyrie.miaosha.redis;

/**
 * @author kyrie
 * @date 2019-12-23 - 23:20
 */
public class GoodsKey extends BasePrefix {

    private GoodsKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    // 页面缓存有效期设置为60s
    public static GoodsKey getGoodsList = new GoodsKey(60,"gl");
    public static GoodsKey getGoodsDetail = new GoodsKey(60,"gd");
    public static GoodsKey getMiaoshaGoodsStock = new GoodsKey(0,"gs"); // 获取秒杀商品库存
}

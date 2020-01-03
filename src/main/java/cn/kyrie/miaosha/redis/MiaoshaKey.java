package cn.kyrie.miaosha.redis;

/**
 * @author kyrie
 * @date 2019-12-23 - 23:20
 */
public class MiaoshaKey extends BasePrefix {

    private MiaoshaKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static MiaoshaKey isGoodsOver = new MiaoshaKey(0,"go"); // 永不过期
    public static MiaoshaKey getMiaoshaPath = new MiaoshaKey(60,"mp"); // 永不过期
}

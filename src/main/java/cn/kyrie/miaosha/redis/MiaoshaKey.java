package cn.kyrie.miaosha.redis;

/**
 * @author kyrie
 * @date 2019-12-23 - 23:20
 */
public class MiaoshaKey extends BasePrefix {

    private MiaoshaKey(String prefix) {
        super(prefix);
    }

    public static MiaoshaKey isGoodsOver = new MiaoshaKey("go"); // 永不过期
}

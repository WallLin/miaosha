package cn.kyrie.miaosha.redis;

/**
 * @author kyrie
 * @date 2019-12-23 - 23:20
 */
public class MiaoshaUserKey extends BasePrefix {

    private static final int COOKIE_EXPIRE = 36000 * 24 * 2; // 两天

    private MiaoshaUserKey(String prefix) {
        super(COOKIE_EXPIRE, prefix);
    }

    public static MiaoshaUserKey token = new MiaoshaUserKey("tk");
}

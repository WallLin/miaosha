package cn.kyrie.miaosha.redis;

/**
 * @author kyrie
 * @date 2019-12-23 - 23:20
 */
public class MiaoshaUserKey extends BasePrefix {

    private static final int COOKIE_EXPIRE = 36000 * 24 * 2; // 两天

    private MiaoshaUserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static MiaoshaUserKey token = new MiaoshaUserKey(COOKIE_EXPIRE, "tk");
    // 对象缓存，希望永久不过期
    public static MiaoshaUserKey getById = new MiaoshaUserKey(0, "id");
}

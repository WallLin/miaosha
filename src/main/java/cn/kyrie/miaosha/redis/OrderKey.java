package cn.kyrie.miaosha.redis;

/**
 * @author kyrie
 * @date 2019-12-24 - 14:54
 */
public class OrderKey extends BasePrefix {

    public OrderKey(String prefix) {
        super(prefix);
    }

    public OrderKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
}

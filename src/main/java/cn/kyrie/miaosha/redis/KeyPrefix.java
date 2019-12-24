package cn.kyrie.miaosha.redis;

/**
 * redis key 前缀
 * @author kyrie
 * @date 2019-12-23 - 23:10
 */
public interface KeyPrefix {
    public int expireSeconds(); //设置过期时间
    public String getPrefix();  //获取前缀
}

package cn.kyrie.miaosha.util;

import java.util.UUID;

/**
 * 生成UUID工具类
 * @author kyrie
 * @date 2019-12-25 - 17:04
 */
public class UUIDUtil {
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}

package cn.kyrie.miaosha.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author kyrie
 * @date 2019-12-24 - 22:07
 */
public class ValidatorUtil {

    private static final Pattern mobile_pattern = Pattern.compile("1\\d{10}");

    public static boolean isMobile(String src) {
        if (StringUtils.isEmpty(src)) {
            return false;
        }
        Matcher matcher = mobile_pattern.matcher(src);

        return matcher.matches();
    }

    public static void main(String[] args) {
        System.out.println(isMobile("18812345678"));
        System.out.println(isMobile("188123456"));
    }
}

package cn.kyrie.miaosha.util;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author kyrie
 * @date 2019-12-24 - 19:58
 */
public class MD5Util {

    public static String md5(String str) {
        return DigestUtils.md5Hex(str);
    }

    private static String salt = "1a2b3c4d"; // 固定盐

    public static String inputPassToFormPass(String inputPass) {
        String str = "" + salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    public static String formPassToDBPass(String formPass, String salt) {
        String str = "" + salt.charAt(0) + salt.charAt(2) + formPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    public static String inputPassToDbPass(String inputPass, String salt) {
        String formPass = inputPassToFormPass(inputPass);
        return formPassToDBPass(formPass, salt);
    }

    public static void main(String[] args) {
        System.out.println(inputPassToFormPass("123456")); // d3b1294a61a07da9b49b6e22b2cbd7f9
        System.out.println(formPassToDBPass(inputPassToFormPass("123456"), "1a2b3c4d")); // b7797cce01b4b131b433b6acf4add449
        System.out.println(inputPassToDbPass("123456", "1a2b3c4d"));
    }
}

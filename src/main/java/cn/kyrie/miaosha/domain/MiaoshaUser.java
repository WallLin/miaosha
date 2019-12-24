package cn.kyrie.miaosha.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author kyrie
 * @date 2019-12-24 - 22:32
 */
@Data
public class MiaoshaUser {
    private Long id; // 用户ID，手机号码
    private String nickname;
    private String password; // MD5(MD5(pass明文+固定salt) + salt)
    private String salt;
    private String head; // 头像，云存储的ID
    private Date registerDate; // 注册时间
    private Date lastLoginDate; // 上次登录时间
    private Integer loginCount; // 登录次数
}

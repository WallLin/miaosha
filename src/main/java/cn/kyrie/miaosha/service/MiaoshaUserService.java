package cn.kyrie.miaosha.service;

import cn.kyrie.miaosha.dao.MiaoshaUserDao;
import cn.kyrie.miaosha.domain.MiaoshaUser;
import cn.kyrie.miaosha.exception.GlobalException;
import cn.kyrie.miaosha.redis.MiaoshaUserKey;
import cn.kyrie.miaosha.redis.RedisService;
import cn.kyrie.miaosha.result.CodeMsg;
import cn.kyrie.miaosha.util.MD5Util;
import cn.kyrie.miaosha.util.UUIDUtil;
import cn.kyrie.miaosha.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * @author kyrie
 * @date 2019-12-24 - 22:35
 */
@Service
public class MiaoshaUserService {

    public static final String COOKIE_NAME_TOKEN = "token";

    @Autowired
    MiaoshaUserDao miaoshaUserDao;

    @Autowired
    RedisService redisService;

    /**
     * 根据token获取用户信息
     * @param token
     * @return
     */
    public MiaoshaUser getByToken(String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        return redisService.get(MiaoshaUserKey.token, token, MiaoshaUser.class);
    }

    /**
     * 用户登录
     *
     * @param response
     * @param loginVo
     * @return
     */
    public boolean login(HttpServletResponse response, LoginVo loginVo) {
        if (loginVo == null) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();

        // 判断用户是否存在
        MiaoshaUser user = miaoshaUserDao.getById(Long.parseLong(mobile));
        if (user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        // 验证密码
        String dbPass = user.getPassword();
        String dbSalt = user.getSalt();
        String calcPass = MD5Util.formPassToDBPass(formPass, dbSalt);
        if (!calcPass.equals(dbPass)) {
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        // 生成cookie
        String token = UUIDUtil.uuid();
        redisService.set(MiaoshaUserKey.token, token, user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds()); // 设置cookie有效期
        cookie.setPath("/");
        response.addCookie(cookie);
        return true;
    }
}

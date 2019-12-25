package cn.kyrie.miaosha.service;

import cn.kyrie.miaosha.dao.MiaoshaUserDao;
import cn.kyrie.miaosha.domain.MiaoshaUser;
import cn.kyrie.miaosha.exception.GlobalException;
import cn.kyrie.miaosha.result.CodeMsg;
import cn.kyrie.miaosha.util.MD5Util;
import cn.kyrie.miaosha.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author kyrie
 * @date 2019-12-24 - 22:35
 */
@Service
public class MiaoshaUserService {

    @Autowired
    MiaoshaUserDao miaoshaUserDao;

    /**
     * 用户登录
     * @param loginVo
     * @return
     */
    public boolean login(LoginVo loginVo) {
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
        return true;
    }
}

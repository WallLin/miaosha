package cn.kyrie.miaosha.controller;

import cn.kyrie.miaosha.result.CodeMsg;
import cn.kyrie.miaosha.result.Result;
import cn.kyrie.miaosha.service.MiaoshaUserService;
import cn.kyrie.miaosha.util.ValidatorUtil;
import cn.kyrie.miaosha.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author kyrie
 * @date 2019-12-22 - 10:52
 */
@Controller
@RequestMapping("/login")
public class LoginController {

    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    MiaoshaUserService miaoshaUserService;

    @RequestMapping("/to_login")
    public String toLogin () {
        return "login";
    }

    @RequestMapping("/do_login")
    @ResponseBody
    public Result<CodeMsg> doLogin (LoginVo loginVo) {
        log.info(loginVo.toString());
        // 参数校验
        if (loginVo == null) {
            return Result.error(CodeMsg.SERVER_ERROR);
        } else {
            String mobile = loginVo.getMobile();
            String inputPass = loginVo.getPassword();
            if (StringUtils.isEmpty(inputPass)) {
                return Result.error(CodeMsg.PASSWORD_EMPTY);
            }
            if (StringUtils.isEmpty(mobile)) {
                return Result.error(CodeMsg.MOBILE_EMPTY);
            }
            if (!ValidatorUtil.isMobile(mobile)) {
                return Result.error(CodeMsg.MOBILE_ERROR);
            }
        }
        // 登录
        CodeMsg cm = miaoshaUserService.login(loginVo);
        if (cm.getCode() == 0) {
            return Result.success(CodeMsg.SUCCESS);
        } else {
            return Result.error(cm);
        }
    }
}

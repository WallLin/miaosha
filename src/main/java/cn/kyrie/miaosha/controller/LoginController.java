package cn.kyrie.miaosha.controller;

import cn.kyrie.miaosha.result.Result;
import cn.kyrie.miaosha.service.MiaoshaUserService;
import cn.kyrie.miaosha.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

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
    public Result<Boolean> doLogin (@Valid LoginVo loginVo) {
        log.info(loginVo.toString());
        // 登录
        miaoshaUserService.login(loginVo);
        return Result.success(true);
    }
}

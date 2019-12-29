package cn.kyrie.miaosha.controller;

import cn.kyrie.miaosha.domain.MiaoshaUser;
import cn.kyrie.miaosha.result.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * JMeter压测测试
 *
 * 5000 * 10
 *
 * error: 1: 0.15%; 2: 0.01%; 3: 0.02%
 *
 * QPS: 1: 1824; 2: 2361; 3: 2317
 *
 * @author kyrie
 * @date 2019-12-22 - 10:52
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @RequestMapping("/info")
    @ResponseBody
    public Result<MiaoshaUser> info(MiaoshaUser user) {
        return Result.success(user);
    }
}

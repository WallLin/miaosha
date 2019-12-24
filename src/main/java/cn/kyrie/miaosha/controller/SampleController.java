package cn.kyrie.miaosha.controller;

import cn.kyrie.miaosha.domain.User;
import cn.kyrie.miaosha.redis.RedisService;
import cn.kyrie.miaosha.redis.UserKey;
import cn.kyrie.miaosha.result.Result;
import cn.kyrie.miaosha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author kyrie
 * @date 2019-12-22 - 10:52
 */
@Controller
@RequestMapping("/demo")
public class SampleController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisService redisService;

    @RequestMapping("/do/get")
    @ResponseBody
    public Result<User> doGet () {
        User user = userService.getUserById(1);
        return Result.success(user);
    }

    @RequestMapping("/do/tx")
    @ResponseBody
    public Result<Boolean> doTx () {
        boolean flag = userService.insert();
        return Result.success(flag);
    }

    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<User> redisGet () {
        User user = redisService.get(UserKey.getById, "" + 1, User.class);
        return Result.success(user);
    }

    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<Boolean> redisSet () {
        User user = new User();
        user.setId(1);
        user.setName("kyrie");
        redisService.set(UserKey.getById, "" + 1, user);
        return Result.success(true);
    }
}

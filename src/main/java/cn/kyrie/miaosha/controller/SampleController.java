package cn.kyrie.miaosha.controller;

import cn.kyrie.miaosha.domain.User;
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


    @RequestMapping("/thymeleaf")
    public String thymeleaf(Model model) {
        model.addAttribute("name", "kyrie");
        return "hello";
    }

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
}

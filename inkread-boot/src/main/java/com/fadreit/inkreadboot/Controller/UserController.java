package com.fadreit.inkreadboot.Controller;

import com.fadreit.inkreadboot.Common.Result;
import com.fadreit.inkreadboot.Dto.User_FixPassword;
import com.fadreit.inkreadboot.Dto.User_Register;
import com.fadreit.inkreadboot.Entity.User;
import com.fadreit.inkreadboot.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用于处理用户相关的请求
 */
@Slf4j
@RestController
public class UserController {

    //自动注入Service层
    @Autowired
    private UserService userService;

    /**
     * 用户注册（无需鉴权）
     * 参数：User
     * 返回：Result
     */
    @PostMapping("/api/users/register")
    public Result register(@RequestBody User user) {
        //日志记录
        log.info("用户注册：{}", user);
        //调用Service层方法
        Result res = userService.register(user);
        //返回结果
        return res;
    }

    /**
     * 用户登录（无需鉴权）
     * 参数：User
     * 返回：Result
     */
    @PostMapping("/api/users/login")
    public Result login(@RequestBody User user) {
        //日志记录
        log.info("用户登录：{}", user);
        //调用Service层方法
        Result res = userService.login(user);
        return res;
    }

    /**
     * 获取用户信息（需要鉴权）
     * 登录成功后，客户端会返回一个token，用于后续的请求
     * 携带token，访问此接口，即可获取用户信息
     * 登录失败，返回401
     * @return Result
     */
    @GetMapping("/api/users/profile")
    public Result profile() {
        //日志记录
        log.info("获取用户信息");
        //调用Service层方法
        Result res = userService.profile();
        return res;
    }

    /**
     * 修改用户信息（需要鉴权）
     * 登录成功后，客户端会返回一个token，用于后续的请求
     * 携带token，访问此接口，即可修改用户信息
     * 登录失败，返回401
     * @param user 用户信息
     * @return Result
     */
    @PutMapping("/api/users/profile")
    public Result updateProfile(@RequestBody User user) {
        //日志记录
        log.info("修改用户信息：{}", user);
        //调用Service层方法
        Result res = userService.updateProfile(user);
        return res;
    }

    @PutMapping("/api/users/password")
    public Result fixPassword(@RequestBody User_FixPassword user) {
        //日志记录
        log.info("修改密码：{}", user);
        //调用Service层方法
        Result res = userService.fixPassword(user);
        return res;
    }
}

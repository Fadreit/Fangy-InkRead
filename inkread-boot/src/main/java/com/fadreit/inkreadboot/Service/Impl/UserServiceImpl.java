package com.fadreit.inkreadboot.Service.Impl;

import cn.dev33.satoken.stp.StpUtil;
import com.fadreit.inkreadboot.Common.Result;
import com.fadreit.inkreadboot.Dto.User_FixPassword;
import com.fadreit.inkreadboot.Dto.User_Login;
import com.fadreit.inkreadboot.Dto.User_Register;
import com.fadreit.inkreadboot.Entity.User;
import com.fadreit.inkreadboot.Mapper.UserMapper;
import com.fadreit.inkreadboot.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class UserServiceImpl implements UserService {
    //注入Mapper
    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional //事务
    public Result register(User user) {
        //调用Mapper
        //先查询用户名是否存在
        Integer count = userMapper.queryUsername(user.getUsername());
        //如果存在，则返回错误信息
        if (count > 0) {
            return Result.error(400,"用户名已存在");
        }
        //如果不存在，则创建用户信息
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.insertUser(user);
        //查询用户信息并返回响应参数
        User now_user = userMapper.queryUser(user.getUsername(),user.getPassword());
        User_Register user_register = new User_Register(
                now_user.getId(),now_user.getUsername(),now_user.getNickname(),user.getCreatedAt());
        return Result.success(user_register);
    }

    @Override
    @Transactional
    public Result login(User user) {
        //调用Mapper层
        //先查询账号是否存在
        //后期修改（合并401/402）
        Integer count = userMapper.queryUsername(user.getUsername());
        if (count == 0){
            return Result.error(401,"账号不存在,请注册账号");
        }
        //账号存在，先查询密码是否正确
        User now_user = userMapper.queryUser(user.getUsername(),user.getPassword());
        if (!now_user.getPassword().equals(user.getPassword())){
            return Result.error(402,"用户名或密码错误");
        }
        //密码正确，查询用户是否被禁用
        if (now_user.getStatus() == 0){
            return Result.error(403,"账号已被禁用，请联系管理员");
        }
        //账号密码正确，生成token
        //利用sa-token登录会话
        StpUtil.login(now_user.getId());
        //存储用户信息(id,username)
        StpUtil.getSession().set("userId",now_user.getId());
        StpUtil.getSession().set("username",now_user.getUsername());
        //利用sa-token获取token
        String token = StpUtil.getTokenValue();
        User_Login ul = new User_Login(token,now_user);
        //返回响应参数
        return Result.success(ul);
    }

    @Override
    public Result profile() {
        //调用Mapper层
        Integer id = StpUtil.getLoginIdAsInt();
        User user = userMapper.queryUserById(id);
        user.setPassword(null);
        //返回用户信息
        return Result.success(user);
    }

    @Override
    @Transactional
    public Result updateProfile(User user) {
        //调用Mapper层
        user.setUpdatedAt(LocalDateTime.now());
        //获取id
        Integer id = StpUtil.getLoginIdAsInt();
        user.setId(id);
        userMapper.updateProfile(user);
        //查询用户信息
        User new_user = userMapper.queryUserById(id);
        return Result.success(new_user);
    }

    @Override
    @Transactional
    public Result fixPassword(User_FixPassword user) {
        //调用Mapper层
        Integer id = StpUtil.getLoginIdAsInt();
        User now_user = userMapper.queryUserById(id);
        //查询用户密码
        String password = userMapper.queryUserpassword(id);
        now_user.setPassword(password);
        //先判断原密码是否正确
        if (!now_user.getPassword().equals(user.getOldPassword())){
            return Result.error(400,"原密码错误");
        }
        //原密码正确，修改密码
        if (!user.getNewPassword().equals(user.getConfirmPassword())){
            return Result.error(400,"新密码不一致");
        }
        now_user.setPassword(user.getNewPassword());
        userMapper.fixPassword(now_user);
        //返回响应参数
        //登出服务
        StpUtil.logout();
        return Result.success("密码修改成功，请重新登录");
    }

}

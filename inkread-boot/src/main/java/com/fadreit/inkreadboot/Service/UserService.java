package com.fadreit.inkreadboot.Service;

import com.fadreit.inkreadboot.Common.Result;
import com.fadreit.inkreadboot.Dto.User_FixPassword;
import com.fadreit.inkreadboot.Dto.User_Register;
import com.fadreit.inkreadboot.Entity.User;

public interface UserService {
    /**
     * 注册用户
     * 参数：User
     * 返回：Result
     */
    Result register(User user);

    /**
     * 登录用户
     * 参数：User
     * 返回：Result
     */
    Result login(User user);

    /**
     * 通过token获取用户信息
     * 登录后才能访问
     * 登录成功后，返回用户信息
     * @return Result
     */
    Result profile();

    /**
     * 修改用户信息
     * 登录后才能访问
     * 登录成功后，修改用户信息
     * @param user 用户信息
     * @return Result
     */
    Result updateProfile(User user);

    /**
     * 修改密码
     * 登录后才能访问
     * 登录成功后，修改密码
     * @param user 用户信息
     * @return Result
     */
    Result fixPassword(User_FixPassword user);
}

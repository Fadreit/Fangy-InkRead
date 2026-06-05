package com.fadreit.inkreadboot.Mapper;

import com.fadreit.inkreadboot.Entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    /**
     * 查询用户名
     * @param username 用户名
     * @return count
     */
    Integer queryUsername(String username);

    /**
     * 插入用户
     * @param user 用户
     */
    void insertUser(User user);

    /**
     * 通过用户名和密码查询用户
     * @param username 用户名
     * @param password 密码
     * @return 用户
     */
    User queryUser(String username, String password);

    /**
     * 通过id查询用户
     * @param id id
     * @return 用户
     */
    User queryUserById(Integer id);

    /**
     * 更新用户信息
     *
     * @param user 用户
     * @return
     */
    void updateProfile(User user);

    /**
     * 修改密码
     * @param user 用户
     */
    void fixPassword(User user);

    /**
     * 查询用户密码
     * @return 用户密码
     */
    String queryUserpassword(Integer id);
}

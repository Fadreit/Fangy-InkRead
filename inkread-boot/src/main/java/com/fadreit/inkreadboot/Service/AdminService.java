package com.fadreit.inkreadboot.Service;

import com.fadreit.inkreadboot.Common.Result;
import com.fadreit.inkreadboot.Dto.Admin_PageListUsersRequest;

public interface AdminService {
    /**
     * 分页查询用户列表
     * @param request 请求参数
     * @return 用户列表
     */
    Result PageListUsers(Admin_PageListUsersRequest request);

    /**
     * 通过id查询用户
     * @param id 用户id
     * @return 用户
     */
    Result GetUserById(Integer id);

    /**
     * 修改用户状态
     * @param id 用户id
     * @param status 状态
     * @return 响应结果
     */
    Result UpdateUserStatus(Integer id, Integer status);

    /**
     * 修改用户角色
     * @param id 用户id
     * @param role 角色
     * @return 响应结果
     */
    Result UpdateUserRole(Integer id, String role);

    /**
     * 获取仪表盘数据
     * @return 仪表盘数据
     */
    Result GetDashboard();
}

package com.fadreit.inkreadboot.Mapper;

import com.fadreit.inkreadboot.Dto.Admin_DashBoardReturn;
import com.fadreit.inkreadboot.Dto.Admin_PageListUsersRequest;
import com.fadreit.inkreadboot.Entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AdminMapper {
    /**
     * 分页查询用户列表
     * @param request 请求参数
     * @return 用户列表
     */
    List<User> PageListUsers(Admin_PageListUsersRequest request);

    /**
     * 通过Id查询用户
     * @param id 用户id
     * @return 用户
     */
    User GetUserById(Integer id);

    /**
     * 修改用户状态
     *
     * @param id     用户id
     * @param status 状态
     */
    void UpdateUserStatus(@Param("id") Integer id, @Param("status") Integer status, @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * 修改用户角色
     *
     * @param id     用户id
     * @param role 角色
     * @param now 当前时间
     */
    void UpdateUserRole(@Param("id") Integer id, @Param("role") String role, @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * 查询用户数
     * @return 用户数
     */
    Integer GetUserCount();

    /**
     * 查询分类数
     * @return 分类数
     */
    Integer GetCategoryCount();

    /**
     * 获取订单数
     * @return 订单数
     */
    Integer GetPendingOrderCount();

    /**
     * 获取图书数
     * @return 图书数
     */
    Integer GetBookCount();

    /**
     * 获取最近订单
     * @return 最近订单
     */
    List<Admin_DashBoardReturn> GetRecentOrders();
}

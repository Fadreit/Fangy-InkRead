package com.fadreit.inkreadboot.Service.Impl;

import cn.dev33.satoken.stp.StpUtil;
import com.fadreit.inkreadboot.Common.DashBoardResult;
import com.fadreit.inkreadboot.Common.PageResult;
import com.fadreit.inkreadboot.Common.Result;
import com.fadreit.inkreadboot.Dto.Admin_DashBoardReturn;
import com.fadreit.inkreadboot.Dto.Admin_PageListUsersRequest;
import com.fadreit.inkreadboot.Entity.User;
import com.fadreit.inkreadboot.Mapper.AdminMapper;
import com.fadreit.inkreadboot.Service.AdminService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class AdminServiceImpl implements AdminService {

    // 注入Mapper
    @Autowired
    private AdminMapper adminMapper;

    @Override
    public Result PageListUsers(Admin_PageListUsersRequest request) {
        //判断页码数量是否为空
        if (request.getPage() == null) request.setPage(1);
        if (request.getSize() == null) request.setSize(10);
        //开始分页
        PageHelper.startPage(request.getPage(), request.getSize());
        //调用Mapper查询用户列表
        List<User> list = adminMapper.PageListUsers(request);
        //强制转换为Page<User>
        Page<User> page = (Page<User>) list;
        //设置PageResult
        PageResult<User> res = new PageResult<>(
                page.getTotal(), page.getPages(), page.getPageNum(), page.getPageSize(), page.getResult());
        //返回结果
        return Result.success(res);
    }

    @Override
    public Result GetUserById(Integer id) {
        User user = adminMapper.GetUserById(id);
        return Result.success(user);
    }

    @Override
    @Transactional
    public Result UpdateUserStatus(Integer id, Integer status) {
        //先获取当前用户Id
        Integer userId = StpUtil.getLoginIdAsInt();
        if (userId.equals(id)){
            return Result.error(400,"不能修改自己的状态");
        }
        //查询用户
        User user = adminMapper.GetUserById(id);
        if (user == null || user.getIsDeleted() == 1){
            return Result.error(404,"用户不存在");
        }
        //调用Mapper执行
        LocalDateTime now = LocalDateTime.now();
        adminMapper.UpdateUserStatus(id, status, now);
        return Result.success();
    }

    @Override
    public Result UpdateUserRole(Integer id, String role) {
        //先获取当前用户Id
        Integer userId = StpUtil.getLoginIdAsInt();
        if (userId.equals(id)){
            return Result.error(400,"不能修改自己的角色");
        }
        //查询用户
        User user = adminMapper.GetUserById(id);
        if (user == null || user.getIsDeleted() == 1){
            return Result.error(404,"用户不存在");
        }
        //调用Mapper执行
        LocalDateTime now = LocalDateTime.now();
        adminMapper.UpdateUserRole(id, role, now);
        return Result.success();
    }

    @Override
    @Transactional
    public Result GetDashboard() {
        //查询用户数
        Integer userCount = adminMapper.GetUserCount();
        //查询分类数
        Integer categoryCount = adminMapper.GetCategoryCount();
        //查询pending订单数
        Integer pendingOrderCount = adminMapper.GetPendingOrderCount();
        //查询图书数
        Integer bookCount = adminMapper.GetBookCount();
        //封装数据
        List<Admin_DashBoardReturn> list = adminMapper.GetRecentOrders();
        //封装Common
        DashBoardResult<Admin_DashBoardReturn> result = new DashBoardResult<>(
                bookCount,
                categoryCount,
                pendingOrderCount,
                userCount,
                list);
        return Result.success(result);
    }
}

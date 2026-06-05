package com.fadreit.inkreadboot.Controller;

import com.fadreit.inkreadboot.Common.Result;
import com.fadreit.inkreadboot.Dto.Admin_PageListUsersRequest;
import com.fadreit.inkreadboot.Service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class AdminController {

    //注入Service层
    @Autowired
    private AdminService adminService;

    /**
     * 分页查询用户列表
     * @param request 请求参数
     * @return 用户列表
     */
    @GetMapping("/api/admin/users")
    public Result PageListUsers(Admin_PageListUsersRequest  request){
        //日志记录
        log.info("分页查询用户：{}", request);
        return adminService.PageListUsers(request);
    }

    /**
     * 通过Id查询用户
     * @param id 用户id
     * @return 用户
     */
    @GetMapping("/api/admin/uses/{id}")
    public Result GetUserById(@PathVariable Integer id){
        //日志记录
        log.info("查询用户：{}", id);
        return adminService.GetUserById(id);
    }

    /**
     * 更新用户状态
     * @param id 用户id
     * @param status 状态
     * @return 响应结果
     */
    @PutMapping("/api/admin/users/{id}/status")
    public Result UpdateUserStatus(@PathVariable Integer id, @RequestParam Integer status){
        //日志记录
        log.info("更新用户状态：{}", id);
        return adminService.UpdateUserStatus(id, status);
    }

    /**
     * 更新用户角色
     * @param id 用户id
     * @param role 角色
     * @return 响应结果
     */
    @PutMapping("/api/admin/users/{id}/role")
    public Result UpdateUserRole(@PathVariable Integer id, @RequestParam String role){
        //日志记录
        log.info("更新用户角色：{}", id);
        return adminService.UpdateUserRole(id, role);
    }

    @GetMapping("/api/admin/dashboard")
    public Result GetDashboard(){
        //日志记录
        log.info("获取仪表盘数据");
        return adminService.GetDashboard();
    }
}

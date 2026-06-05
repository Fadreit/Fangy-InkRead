package com.fadreit.inkreadboot.Config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

/**
 * Sa-Token 配置类
 */
@Configuration // 标记配置类
public class SaTokenConfig {
    // 注册 Sa-Token 配置
    // 登录拦截器
    public void addInterceptorsLogin(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> {
            SaRouter.match("/**")
                    .notMatch("/api/users/register",
                            "/api/users/login",
                            "/api/books",
                            "/api/books/{id}"
                    )
                    .check(r -> cn.dev33.satoken.stp.StpUtil.checkLogin());
        })).addPathPatterns("/**");
    }

    //权限拦截器
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> {

            // 管理员接口
            SaRouter.match("/api/admin/**")
                    .check(r -> StpUtil.checkRole("admin"));

        })).addPathPatterns("/**");
    }
}

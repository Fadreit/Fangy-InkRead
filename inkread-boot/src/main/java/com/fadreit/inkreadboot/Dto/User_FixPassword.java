package com.fadreit.inkreadboot.Dto;

import lombok.Data;

/**
 * 修改密码的请求数据
 */
@Data
public class User_FixPassword {
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}

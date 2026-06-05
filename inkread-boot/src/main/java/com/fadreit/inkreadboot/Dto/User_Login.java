package com.fadreit.inkreadboot.Dto;

import com.fadreit.inkreadboot.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 用户登录的响应数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User_Login {
    private String token;
    private User user;
}

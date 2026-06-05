package com.fadreit.inkreadboot.Dto;

import lombok.Data;

@Data
public class UpdateUserStatusReturn {
    private Integer id;
    private String username;
    private Integer status;
}

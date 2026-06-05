package com.fadreit.inkreadboot.Dto;

import lombok.Data;

/**
 * 书籍的响应数据
 * 修改书籍状态
 */
@Data
public class Book_UpdateStatus {
    private Integer id;
    private String title;
    private Integer status;
}

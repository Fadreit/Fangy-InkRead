package com.fadreit.inkreadboot.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 书籍的请求数据
 * 管理员查看书籍列表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book_PageListBook {
    private Integer page = 1;
    private Integer size = 10;
    private String keyword;
    private Integer categoryId;
    private Integer status;
}

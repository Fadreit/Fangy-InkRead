package com.fadreit.inkreadboot.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分类的响应数据
 * 用户查看分类列表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category_ShowCategories {
    private Integer id;
    private String name;
    private String description;
    private Integer sortOrder;
}

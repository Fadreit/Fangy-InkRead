package com.fadreit.inkreadboot.Service;

import com.fadreit.inkreadboot.Common.Result;
import com.fadreit.inkreadboot.Entity.Category;

public interface CategoryService {
    /**
     * 添加分类
     * @param category 分类
     * @return 响应结果
     */
    Result addCategory(Category category);

    /**
     * 列出所有分类
     * @return 响应结果
     */
    Result listCategories();

    /**
     * 显示所有分类(status = 1 and not show 数量)
     * @return 响应结果
     */
    Result showCategories();

    /**
     * 修改分类
     * @param category 分类
     * @return 响应结果
     */
    Result updateCategory(Category category);

    /**
     * 删除分类
     * @param id 分类id
     * @return 响应结果
     */
    Result deleteCategory(Integer id);

    /**
     * 修改分类状态
     * @param id 分类id
     * @param status 分类状态
     * @return 响应结果
     */
    Result updateCategoryStatus(Integer id, Integer status);
}

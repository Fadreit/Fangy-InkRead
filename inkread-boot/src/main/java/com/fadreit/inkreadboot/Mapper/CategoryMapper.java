package com.fadreit.inkreadboot.Mapper;

import com.fadreit.inkreadboot.Dto.Category_ShowCategories;
import com.fadreit.inkreadboot.Entity.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {
    /**
     * 查询分类名的数量
     * @param name 分类名
     * @return count
     */
    Integer queryCategoryNameCount(String name);

    /**
     * 添加分类
     * @param category 分类
     */
    void addCategory(Category category);

    /**
     * 查询所有分类
     * @return 分类列表
     */
    List<Category> listCategories();

    /**
     * 显示所有分类
     * @return 分类列表
     */
    List<Category_ShowCategories> showCategories();

    /**
     * 修改分类
     * @param category 分类
     */
    void updateCategory(Category category);

    /**
     * 根据id查询分类
     * @param id 分类id
     * @return 分类
     */
    Category queryCategoryById(Integer id);

    /**
     * 删除分类
     * @param id 分类id
     */
    void deleteCategory(Integer id);

    /**
     * 修改分类状态
     * @param id 分类id
     * @param status 分类状态
     */
    void updateCategoryStatus(Integer id, Integer status);
}

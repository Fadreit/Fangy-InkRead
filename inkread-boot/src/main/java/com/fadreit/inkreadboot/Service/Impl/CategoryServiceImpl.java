package com.fadreit.inkreadboot.Service.Impl;

import com.fadreit.inkreadboot.Common.Result;
import com.fadreit.inkreadboot.Dto.Category_ShowCategories;
import com.fadreit.inkreadboot.Entity.Category;
import com.fadreit.inkreadboot.Mapper.CategoryMapper;
import com.fadreit.inkreadboot.Service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class CategoryServiceImpl implements CategoryService {

    //自动注入Mapper
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public Result addCategory(Category category) {
        //调用Mapper层
        //先查询名称是否存在
        Integer count = categoryMapper.queryCategoryNameCount(category.getName());
        if (count > 0) {
            return Result.error(400, "分类名称已存在");
        }
        //如果不存在，则添加
        //设置参数
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        categoryMapper.addCategory(category);
        //返回结果
        return Result.success(category);
    }

    @Override
    public Result listCategories() {
        //调用Mapper层
        List<Category> list = categoryMapper.listCategories();
        return Result.success(list);
    }

    @Override
    public Result showCategories() {
        //调用Mapper层
        List<Category_ShowCategories> list = categoryMapper.showCategories();
        return Result.success(list);
    }

    @Override
    @Transactional
    public Result updateCategory(Category category) {
        //调用Mapper层
        //设置参数
        category.setUpdatedAt(LocalDateTime.now());
        categoryMapper.updateCategory(category);
        //查询新分类
        Category new_category = categoryMapper.queryCategoryById(category.getId());
        //返回结果
        return Result.success(new_category);
    }

    @Override
    public Result deleteCategory(Integer id) {
        //调用Mapper层
        //如果分类不存在，则返回错误信息
        Category category = categoryMapper.queryCategoryById(id);
        if (category == null || category.getIsDeleted() == 1) {
            return Result.error(404, "分类不存在");
        }
        //如果分类下还有图书
        if (category.getBookCount() > 0) {
            return Result.error(400, "分类下还有图书，请先删除图书");
        }
        //删除分类
        categoryMapper.deleteCategory(id);
        //返回结果
        return Result.success();
    }

    @Override
    public Result updateCategoryStatus(Integer id, Integer status) {
        //调用Mapper层
        categoryMapper.updateCategoryStatus(id, status);
        //返回结果
        return Result.success();
    }

}

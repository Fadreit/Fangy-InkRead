package com.fadreit.inkreadboot.Controller;

import com.fadreit.inkreadboot.Common.Result;
import com.fadreit.inkreadboot.Entity.Category;
import com.fadreit.inkreadboot.Service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 分类的响应类
 */

@Slf4j
@RestController
public class CategoryController {

    //自动注入Service
    @Autowired
    private CategoryService categoryService;

    /**
     * 添加分类
     * @param category
     * @return
     * 需要admin权限
     */
    @PostMapping("/api/admin/categories")
    public Result addCategory(@RequestBody Category category) {
        //日志记录
        log.info("添加分类：{}", category);
        //调用Service
        Result result = categoryService.addCategory(category);
        //返回结果
        return result;
    }

    /**
     * 获取所有分类
     * @return
     * 需要admin权限
     */
    @GetMapping("/api/admin/categories")
    public Result listCategories() {
        //日志记录
        log.info("后台获取所有分类");
        //调用Service
        Result result = categoryService.listCategories();
        //返回结果
        return result;
    }

    /**
     * 用户获取所有分类
     * @return
     */
    @GetMapping("/api/categories")
    public Result showCategories() {
        //日志记录
        log.info("用户获取所有分类");
        //调用Service
        Result result = categoryService.showCategories();
        //返回结果
        return result;
    }

    /**
     * 更新分类
     * @param id
     * @param category
     * @return
     * 需要admin权限
     */
    @PutMapping("/api/admin/categories/{id}")
    public Result updateCategory(@PathVariable Integer id, @RequestBody Category category) {
        //日志记录
        log.info("更新分类：{}", category);
        //调用Service
        category.setId(id);
        Result result = categoryService.updateCategory(category);
        //返回结果
        return result;
    }

    /**
     * 删除分类
     * @param id
     * @return
     * 需要admin权限
     */
    @DeleteMapping("/api/admin/categories/{id}")
    public Result deleteCategory(@PathVariable Integer id) {
        //日志记录
        log.info("删除分类：{}", id);
        //调用Service
        Result result = categoryService.deleteCategory(id);
        return result;
    }

    /**
     * 更新分类状态
     * @param id
     * @param status
     * @return
     * 需要admin权限
     */
    @PutMapping("/api/admin/categories/{id}/status")
    public Result updateCategoryStatus(@PathVariable Integer id, @RequestParam Integer status) {
        //日志记录
        log.info("更新分类状态：{}", status);
        //调用Service
        Result result = categoryService.updateCategoryStatus(id,status);
        return result;
    }


}

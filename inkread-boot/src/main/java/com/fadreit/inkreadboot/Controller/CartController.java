package com.fadreit.inkreadboot.Controller;

import com.fadreit.inkreadboot.Common.Result;
import com.fadreit.inkreadboot.Entity.Cart;
import com.fadreit.inkreadboot.Service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车响应类
 */

@Slf4j
@RestController
public class CartController {

    //注入Service
    @Autowired
    private CartService cartService;

    /**
     * 添加购物车
     * @param cart 购物车
     * @return 响应结果
     */
    @PostMapping("/api/cart")
    public Result addCart(Cart cart) {
        //日志记录
        log.info("添加购物车：{}", cart);
        //调用Service
        return cartService.addCart(cart);
    }

    /**
     * 查询购物车列表
     * @return 购物车列表
     */
    @GetMapping("/api/cart")
    public Result getCart() {
        //日志记录
        log.info("当前用户查询购物车列表");
        return cartService.getCart();
    }

    /**
     * 修改购物车数量
     * @param id 购物车id
     * @param quantity 数量
     * @return 响应结果
     */
    @PutMapping("/api/cart/{id}")
    public Result updateQuantity(@PathVariable Integer id, @RequestParam Integer quantity){
        //日志记录
        log.info("修改购物车{}数量：{}", id, quantity);
        //调用Service
        Result res = cartService.updateQuantity(id, quantity);
        //返回结果
        return res;
    }

    /**
     * 删除购物车项
     * @param id 购物车项id
     * @return 响应结果
     */
    @DeleteMapping("/api/cart/{id}")
    public Result deleteCart(@PathVariable Integer id){
        //日志记录
        log.info("删除购物车项：{}", id);
        return cartService.deleteCart(id);
    }

    /**
     * 批量删除购物车项
     * @param ids 购物车项id列表
     * @return 响应结果
     */
    @DeleteMapping("/api/cart")
    public Result deleteCarts(List<Integer> ids){
        //日志记录
        log.info("批量删除购物车项：{}", ids);
        return cartService.deleteCarts(ids);
    }
}

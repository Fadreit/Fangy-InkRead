package com.fadreit.inkreadboot.Service;


import com.fadreit.inkreadboot.Common.Result;
import com.fadreit.inkreadboot.Entity.Cart;

import java.util.List;

/**
 * 购物车服务
 */

public interface CartService {
    /**
     * 添加购物车
     * @param cart 购物车
     * @return 响应结果
     */
    Result addCart(Cart cart);

    /**
     * 获取购物车列表
     * @return 响应结果
     */
    Result getCart();

    /**
     * 修改购物车数量
     * @param id 购物车id
     * @param quantity 数量
     * @return 响应结果
     */
    Result updateQuantity(Integer id, Integer quantity);

    /**
     * 删除购物车项
     * @param id 购物车id
     * @return 响应结果
     */
    Result deleteCart(Integer id);

    /**
     * 批量删除购物车项
     * @param ids 购物车id列表
     * @return 响应结果
     */
    Result deleteCarts(List<Integer> ids);
}

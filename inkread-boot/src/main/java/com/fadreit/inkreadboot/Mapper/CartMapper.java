package com.fadreit.inkreadboot.Mapper;

import com.fadreit.inkreadboot.Dto.Cart_GetCartReturn;
import com.fadreit.inkreadboot.Dto.Cart_addCartReturn;
import com.fadreit.inkreadboot.Entity.Cart;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CartMapper {
    /**
     * 查询购物车中指定图书的数量
     * @param userId 用户id
     * @param bookId 图书id
     * @return 数量
     */
    Integer queryCartBookQuantity(Integer userId, Integer bookId);

    /**
     * 更新购物车
     * 数量/更新时间/总价
     * @param cart
     */
    void updateCart(Cart cart);

    /**
     * 添加购物车
     * @param cart
     */
    void addCart(Cart cart);

    /**
     * 查询购物车中指定图书
     * @param userId 用户id
     * @param bookId 图书id
     * @return 图书
     * 多表查询
     */
    Cart_addCartReturn queryCartBook(Integer userId, Integer bookId);

    /**
     * 查询购物车列表
     * @param userId 用户id
     * @return 购物车列表
     * 多表查询
     */
    List<Cart_GetCartReturn> queryCartList(Integer userId);

    /**
     * 通过Id查询购物车项
     * @param cartId 购物车项id
     * @return 购物车项
     */
    Cart queryCartById(Integer cartId);

    /**
     * 删除购物车项
     * @param id 购物车项id
     */
    void deleteCart(Integer id);

    /**
     * 批量删除购物车项
     * @param ids 购物车项id列表
     */
    void deleteCarts(List<Integer> ids);

    /**
     * 根据用户id查询购物车项id列表
     * @param userId 用户id
     * @return 购物车项id列表
     */
    List<Integer> queryCartIdList(Integer userId);
}

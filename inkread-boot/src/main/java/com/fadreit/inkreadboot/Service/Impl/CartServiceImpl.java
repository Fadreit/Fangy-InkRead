package com.fadreit.inkreadboot.Service.Impl;

import cn.dev33.satoken.stp.StpUtil;
import com.fadreit.inkreadboot.Common.Result;
import com.fadreit.inkreadboot.Dto.Cart_GetCartReturn;
import com.fadreit.inkreadboot.Dto.Cart_addCartReturn;
import com.fadreit.inkreadboot.Entity.Book;
import com.fadreit.inkreadboot.Entity.Cart;
import com.fadreit.inkreadboot.Mapper.BookMapper;
import com.fadreit.inkreadboot.Mapper.CartMapper;
import com.fadreit.inkreadboot.Service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class CartServiceImpl implements CartService {

    //注入Mapper
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private BookMapper bookMapper;

    @Override
    @Transactional
    public Result addCart(Cart cart) {
        //查询图书是否存在或库存是否充足
        Book book = bookMapper.getBookById(cart.getBookId());
        if (book == null || book.getIsDeleted() == 1 || book.getStatus() == 0){
            return Result.error(400,"图书不存在或已下架");
        }
        if (book.getStock() < cart.getQuantity()){
            return Result.error(400,"库存不足，当前库存为" + book.getStock());
        }
        //获取用户ID
        Integer userId =StpUtil.getLoginIdAsInt();
        cart.setUserId(userId);
        //设置时间
        cart.setUpdatedAt(LocalDateTime.now());
        //先判断购物车中是否有当前图书
        Integer count = cartMapper.queryCartBookQuantity(cart.getUserId(), cart.getBookId());
        //如果有,则增加数量
        if (count != null && count > 0){
            cart.setQuantity(cart.getQuantity() + count);
            cart.setSubtotal(book.getPrice().multiply(new BigDecimal(cart.getQuantity())));
            cartMapper.updateCart(cart);
        }
        //如果没有，则增加新cart
        else {
            cart.setCreatedAt(LocalDateTime.now());
            cart.setSubtotal(book.getPrice().multiply(new BigDecimal(cart.getQuantity())));
            cartMapper.addCart(cart);
        }
        //设置返回数据
        Cart_addCartReturn cart_addCartReturn = cartMapper.queryCartBook(cart.getUserId(), cart.getBookId());
        BigDecimal subtotal = cart_addCartReturn.getPrice().multiply(
                new BigDecimal(cart_addCartReturn.getQuantity()));
        cart_addCartReturn.setSubtotal(subtotal);
        return Result.success(cart_addCartReturn);
    }

    @Override
    public Result getCart() {
        Integer userId = StpUtil.getLoginIdAsInt();
        //查询此用户的购物车列表
        List<Cart_GetCartReturn> cart_getCartReturns = cartMapper.queryCartList(userId);
        return Result.success(cart_getCartReturns);
    }

    @Override
    @Transactional
    public Result updateQuantity(Integer cartId, Integer quantity) {
        //获取用户id
        Integer userId = StpUtil.getLoginIdAsInt();
        //查询购物车项
        Cart cart = cartMapper.queryCartById(cartId);
        if (cart == null){
            return Result.error(404,"购物车项不存在");
        }
        //设置时间
        cart.setUpdatedAt(LocalDateTime.now());
        //获取当前图书
        Book book = bookMapper.getBookById(cart.getBookId());
        //判断图书是否存在
        if (book == null || book.getIsDeleted() == 1 || book.getStatus() == 0){
            return Result.error(400,"图书不存在或已下架");
        }
        //判断库存是否充足
        if (book.getStock() < quantity){
            return Result.error(400,"库存不足，当前库存为" + book.getStock());
        }
        //设置数量
        cart.setQuantity(quantity);
        //设置价格
        cart.setSubtotal(book.getPrice().multiply(new BigDecimal(quantity)));
        //更新购物车
        cartMapper.updateCart(cart);
        //返回结果
        return Result.success(cart);
    }

    @Override
    public Result deleteCart(Integer id) {
        //调用Mapper接口实现删除操作
        cartMapper.deleteCart(id);
        return Result.success();
    }

    @Override
    public Result deleteCarts(List<Integer> ids) {
        //调用Mapper接口实现删除操作
        cartMapper.deleteCarts(ids);
        return Result.success();
    }
}

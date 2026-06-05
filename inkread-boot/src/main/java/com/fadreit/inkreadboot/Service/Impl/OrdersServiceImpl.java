package com.fadreit.inkreadboot.Service.Impl;

import cn.dev33.satoken.stp.StpUtil;
import com.fadreit.inkreadboot.Common.OrderResult;
import com.fadreit.inkreadboot.Common.PageResult;
import com.fadreit.inkreadboot.Common.Result;
import com.fadreit.inkreadboot.Dto.*;
import com.fadreit.inkreadboot.Entity.*;
import com.fadreit.inkreadboot.Mapper.BookMapper;
import com.fadreit.inkreadboot.Mapper.CartMapper;
import com.fadreit.inkreadboot.Mapper.OrdersMapper;
import com.fadreit.inkreadboot.Mapper.UserMapper;
import com.fadreit.inkreadboot.Service.OrdersService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Component
public class OrdersServiceImpl implements OrdersService {

    //注入Mapper
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public Result addOrder(Orders_AddOrderRequest request) {
        //若购物车列表为空
        if (request.getCartIds().size() == 0) {
            return Result.error(400,"购物车列表为空");
        }
        //若收货信息不完整
        if (request.getReceiverName() == null || request.getReceiverName().isEmpty()){
            return Result.error(400,"收货信息不完整");
        }
        if (request.getReceiverPhone() == null || request.getReceiverPhone().isEmpty()){
            return Result.error(400,"收货信息不完整");
        }
        if (request.getReceiverAddress() == null || request.getReceiverAddress().isEmpty()){
            return Result.error(400,"收货信息不完整");
        }
        //获取当前用户Id
        Integer userId = StpUtil.getLoginIdAsInt();
        //检测cardIds是否属于当前用户
        //先查询购物车列表ids
        List<Integer> cartIds = cartMapper.queryCartIdList(userId);
        for (Integer cartId : request.getCartIds()) {
            if (!cartIds.contains(cartId)) {
                return Result.error(400,"存在无效的商品项");
            }
        }
        //校验每个商品的库存是否充足,同时计算金额,锁定库存
        BigDecimal totalAmount = new BigDecimal(0);
        for (Integer cartId : request.getCartIds()) {
            Cart cart = cartMapper.queryCartById(cartId);
            Book book = bookMapper.getBookById(cart.getBookId());
            if (book.getStock() < cart.getQuantity()) {
                return Result.error(400,"商品" + book.getTitle() + "库存不足");
            }
            totalAmount = totalAmount.add(cart.getSubtotal());
            bookMapper.updateBookStock(book.getStock() - cart.getQuantity(), cart.getBookId());
        }
        //生成订单号
        Random random = new Random();
        int[] nums = new int[4];
        for (int i = 0; i < 4; i++) {
            nums[i] = random.nextInt(10);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            sb.append(nums[i]);
        }
        LocalDateTime now = LocalDateTime.now();
        String orderNo = "INKREAD" + now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + sb;
        //创建订单
        Orders order = new Orders();
        //设置订单号
        order.setOrderNo(orderNo);
        //设置用户Id
        order.setUserId(userId);
        //设置总金额
        order.setTotalAmount(totalAmount);
        //设置收货信息
        order.setReceiverName(request.getReceiverName());
        order.setReceiverPhone(request.getReceiverPhone());
        order.setReceiverAddress(request.getReceiverAddress());
        //设置订单状态
        order.setStatus("待发货");
        //设置创建时间
        order.setCreatedAt(now);
        order.setUpdatedAt(now);
        //插入订单
        ordersMapper.addOrder(order);
        //回显订单
        Orders nowOrder = ordersMapper.queryOrderByOrderNo(orderNo);
        //遍历商品写入order_item表
        for (Integer cartId : request.getCartIds()){
            Cart cart = cartMapper.queryCartById(cartId);
            Book book = bookMapper.getBookById(cart.getBookId());
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(nowOrder.getId());
            orderItem.setBookId(book.getId());
            orderItem.setBookTitle(book.getTitle());
            orderItem.setBookCover(book.getCoverUrl());
            orderItem.setPrice(book.getPrice());
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setSubtotal(cart.getSubtotal());
            ordersMapper.addOrderItem(orderItem);
        }
        //删除已下单的购物车项
        cartMapper.deleteCarts(request.getCartIds());
        //返回订单
        Orders_AddOrdersReturn returnOrder = new Orders_AddOrdersReturn(
                nowOrder.getId(),
                nowOrder.getOrderNo(),
                nowOrder.getTotalAmount(),
                nowOrder.getStatus(),
                request.getCartIds().size(),
                nowOrder.getCreatedAt()
        );
        //返回成功
        return Result.success(returnOrder);
    }

    @Override
    public Result PageListOrders(Orders_PageListOrdersRequest request) {
        //开始分页
        PageHelper.startPage(request.getPage(), request.getSize());
        //获取当前用户id
        Integer userId = StpUtil.getLoginIdAsInt();
        //查询用户订单
        List<Orders> orders = ordersMapper.PageListOrders(userId, request);
        //强制转换类型
        Page<Orders> page = (Page<Orders>) orders;
        //封装PageResult
        PageResult<Orders> pageResult = new PageResult<>(
                page.getTotal(), page.getPages(), page.getPageNum(), page.getPageSize(), page.getResult());
        //返回结果
        return Result.success(pageResult);
    }

    @Override
    @Transactional
    public Result GetOrderById(Integer id) {
        //先查询当前用户id
        Integer userId = StpUtil.getLoginIdAsInt();
        //查询当前用户
        User user = userMapper.queryUserById(userId);
        //查询订单
        Orders order = ordersMapper.queryOrderById(id);
        //判断当前订单是否属于当前用户
        if (!order.getUserId().equals(userId)) {
            return Result.error(403,"订单不存在");
        }
        //查询订单项
        List<OrderItem> orderItems = ordersMapper.queryOrderItems(id);
        //封装结果
        OrderResult<OrderItem> result = new OrderResult<>(
                order, user.getUsername(), orderItems);
        //返回结果
        return Result.success(result);
    }

    @Override
    @Transactional
    public Result CancelOrder(Integer id) {
        //查询用户Id
        Integer userId = StpUtil.getLoginIdAsInt();
        //查询订单
        Orders order = ordersMapper.queryOrderById(id);
        //判断订单是否属于当前用户
        if (!order.getUserId().equals(userId)) {
            return Result.error(403,"订单不存在");
        }
        //校验订单状态
        if (!order.getStatus().equals("待发货")) {
            return Result.error(400,"订单已发货，请联系管理员退货");
        }
        //业务
        //设置订单状态
        order.setStatus("已取消");
        ordersMapper.updateOrderStatus(id, order.getStatus());
        //回退库存
        //先查询订单项
        List<OrderItem> orderItems = ordersMapper.queryOrderItems(id);
        //遍历订单项
        for (OrderItem orderItem : orderItems) {
            //获取图书Id
            Integer bookId = orderItem.getBookId();
            //获取图书
            Book book = bookMapper.getBookById(bookId);
            //获取数量
            Integer quantity = orderItem.getQuantity() + book.getStock();
            //设置库存
            bookMapper.updateBookStock(quantity, bookId);
        }
        //返回订单
        return Result.success(order);
    }

    @Override
    public Result ConfirmOrder(Integer id) {
        //获取用户 Id
        Integer userId = StpUtil.getLoginIdAsInt();
        //查询订单
        Orders order = ordersMapper.queryOrderById(id);
        //判断订单是否属于当前用户
        if (!order.getUserId().equals(userId)) {
            return Result.error(403,"订单不存在");
        }
        //校验订单状态
        if (!order.getStatus().equals("已发货")) {
            return Result.error(400,"订单状态错误");
        }
        order.setStatus("已完成");
        ordersMapper.updateOrderStatus(id, order.getStatus());
        return Result.success(order);
    }

    @Override
    public Result PageListOrdersForAdmin(Orders_PageListOrdersForAdminRequest request) {
        //开始分页
        PageHelper.startPage(request.getPage(), request.getSize());
        //查询
        List<Orders> orders = ordersMapper.PageListOrdersForAdmin(request);
        //强制转换类型
        Page<Orders> page = (Page<Orders>) orders;
        //封装PageResult
        PageResult<Orders> pageResult = new PageResult<>(
                page.getTotal(), page.getPages(), page.getPageNum(), page.getPageSize(), page.getResult()
        );
        //返回结果
        return Result.success(pageResult);
    }

    @Override
    @Transactional
    public Result Ship(Integer id, Orders_ShipRequest request) {
        //获取当前订单
        Orders order = ordersMapper.queryOrderById(id);
        //判断订单状态
        if (!order.getStatus().equals("待发货")) {
            return Result.error(400,"订单状态错误");
        }
        //设置订单状态
        order.setStatus("已发货");
        //更新订单
        ordersMapper.updateOrderStatus(id, order.getStatus());
        //更新物流信息
        order.setLogisticsCompany(request.getLogisticsCompany());
        order.setLogisticsNo(request.getLogisticsNo());
        ordersMapper.updateOrderLogistics(order);
        //封装返回结果
        Orders_ShipReturn returnOrder = new Orders_ShipReturn(
                order.getId(),
                order.getOrderNo(),
                order.getStatus(),
                order.getLogisticsCompany(),
                order.getLogisticsNo()
        );
        //返回结果
        return Result.success(returnOrder);
    }
}

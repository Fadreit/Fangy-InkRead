package com.fadreit.inkreadboot.Mapper;

import com.fadreit.inkreadboot.Dto.Orders_PageListOrdersForAdminRequest;
import com.fadreit.inkreadboot.Dto.Orders_PageListOrdersRequest;
import com.fadreit.inkreadboot.Entity.OrderItem;
import com.fadreit.inkreadboot.Entity.Orders;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrdersMapper {
    /**
     * 添加订单
     * @param order 订单
     */
    public void addOrder(Orders order);

    /**
     * 根据订单号查询订单
     * @param orderNo 订单号
     * @return 订单
     */
    Orders queryOrderByOrderNo(String orderNo);

    /**
     * 添加订单项
     * @param orderItem 订单项
     */
    void addOrderItem(OrderItem orderItem);

    /**
     * 分页查询当前用户订单
     * @param userId 用户id
     * @param request 请求参数
     * @return 订单列表
     */
    List<Orders> PageListOrders(Integer userId, Orders_PageListOrdersRequest request);

    /**
     * 根据订单Id查询订单
     * @param id 订单Id
     * @return 订单
     */
    Orders queryOrderById(Integer id);

    /**
     * 根据订单Id查询订单项
     * @param id 订单Id
     * @return 订单项列表
     */
    List<OrderItem> queryOrderItems(Integer id);

    /**
     * 修改订单状态
     * @param id 订单Id
     * @param status 订单状态
     */
    void updateOrderStatus(Integer id, String status);

    /**
     * 管理员分页查询订单
     * @param request 请求参数
     * @return 订单列表
     */
    List<Orders> PageListOrdersForAdmin(Orders_PageListOrdersForAdminRequest request);

    /**
     * 修改订单物流信息
     * @param order 订单
     */
    void updateOrderLogistics(Orders order);
}

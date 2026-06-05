package com.fadreit.inkreadboot.Service;

import com.fadreit.inkreadboot.Common.Result;
import com.fadreit.inkreadboot.Dto.Orders_AddOrderRequest;
import com.fadreit.inkreadboot.Dto.Orders_PageListOrdersForAdminRequest;
import com.fadreit.inkreadboot.Dto.Orders_PageListOrdersRequest;
import com.fadreit.inkreadboot.Dto.Orders_ShipRequest;

public interface OrdersService {
    /**
     * 添加订单
     * @return 响应结果
     */
    Result addOrder(Orders_AddOrderRequest  request);

    /**
     * 当前用户分页查询订单
     * @return 响应结果
     */
    Result PageListOrders(Orders_PageListOrdersRequest request);

    /**
     * 根据订单Id查询订单
     * @return 响应结果
     */
    Result GetOrderById(Integer id);

    /**
     * 取消订单
     * @return 响应结果
     */
    Result CancelOrder(Integer id);

    /**
     * 确认收货
     * @return 响应结果
     */
    Result ConfirmOrder(Integer id);

    /**
     * 管理员分页查询订单
     * @return 响应结果
     */
    Result PageListOrdersForAdmin(Orders_PageListOrdersForAdminRequest request);

    /**
     * 发货
     * @return 响应结果
     */
    Result Ship(Integer id, Orders_ShipRequest request);
}

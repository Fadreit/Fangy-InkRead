package com.fadreit.inkreadboot.Controller;

import com.fadreit.inkreadboot.Common.Result;
import com.fadreit.inkreadboot.Dto.Orders_AddOrderRequest;
import com.fadreit.inkreadboot.Dto.Orders_PageListOrdersForAdminRequest;
import com.fadreit.inkreadboot.Dto.Orders_PageListOrdersRequest;
import com.fadreit.inkreadboot.Dto.Orders_ShipRequest;
import com.fadreit.inkreadboot.Service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class OrdersController {

    //注入Service
    @Autowired
    private OrdersService ordersService;

    /**
     * 添加订单
     * @param request
     * @return
     */
    @PostMapping("/api/orders")
    public Result addOrder(Orders_AddOrderRequest  request){
        //日志记录
        log.info("添加订单");
        return ordersService.addOrder(request);
    }

    /**
     * 用户分页查询订单
     * @param request
     * @return
     */
    @GetMapping("/api/orders")
    public Result PageListOrders(Orders_PageListOrdersRequest  request){
        //日志记录
        log.info("当前用户分页查询订单");
        //调用Service
        return ordersService.PageListOrders(request);
    }

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    @GetMapping("/api/orders/{id}")
    public Result GetOrderById(@PathVariable Integer id){
        //日志记录
        log.info("查询订单 {}",id);
        //调用Service
        return ordersService.GetOrderById(id);
    }

    /**
     * 取消订单
     * @param id
     * @return
     */
    @PutMapping("/api/orders/{id}/cancel")
    public Result CancelOrder(@PathVariable Integer id){
        //日志记录
        log.info("取消订单 {}",id);
        //调用Service
        return ordersService.CancelOrder(id);
    }

    /**
     * 确认收货
     * @param id
     * @return
     */
    @PutMapping("/api/orders/{id}/confirm")
    public Result ConfirmOrder(@PathVariable Integer id){
        //日志记录
        log.info("确认收货 {}",id);
        //调用Service
        return ordersService.ConfirmOrder(id);
    }

    /**
     * 管理员分页查询订单
     * @param request
     * @return
     */
    @GetMapping("/api/admin/orders")
    public Result PageListOrdersForAdmin(Orders_PageListOrdersForAdminRequest  request){
        //日志记录
        log.info("管理员分页查询订单");
        //调用Service
        return ordersService.PageListOrdersForAdmin(request);
    }

    @PutMapping("/api/admin/orders/{id}/ship")
    public Result Ship(@PathVariable Integer id, Orders_ShipRequest  request){
        //日志记录
        log.info("发货 {}",id);
        //调用Service
        return ordersService.Ship(id, request);
    }
}

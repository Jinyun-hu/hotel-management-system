package com.hotel.controller;

import com.hotel.common.PageResult;
import com.hotel.common.RestResult;
import com.hotel.dto.OrderDTO;
import com.hotel.query.OrderQuery;
import com.hotel.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单管理控制器
 */
@Slf4j
@Tag(name = "订单管理", description = "订单管理相关接口")
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 查询订单列表
     *
     * @param query 查询条件
     * @return 订单列表
     */
    @Operation(summary = "查询订单列表", description = "获取所有订单列表，支持搜索和筛选")
    @GetMapping
    public RestResult<PageResult<OrderDTO>> listOrders(OrderQuery query) {
        PageResult<OrderDTO> result = orderService.listOrders(query);
        return RestResult.success("success", result);
    }

    /**
     * 新增订单
     *
     * @param orderDTO 订单信息
     * @return 新增的订单
     */
    @Operation(summary = "新增订单", description = "创建新的订单")
    @PostMapping
    public RestResult<OrderDTO> addOrder(@RequestBody OrderDTO orderDTO) {
        OrderDTO result = orderService.addOrder(orderDTO);
        return RestResult.success("订单创建成功", result);
    }

    /**
     * 编辑订单
     *
     * @param id       订单ID
     * @param orderDTO 订单信息
     * @return 更新结果
     */
    @Operation(summary = "编辑订单", description = "修改订单信息")
    @PutMapping("/{id}")
    public RestResult<Void> updateOrder(@PathVariable String id, @RequestBody OrderDTO orderDTO) {
        orderService.updateOrder(id, orderDTO);
        return RestResult.success();
    }

    /**
     * 删除订单
     *
     * @param id 订单ID
     * @return 删除结果
     */
    @Operation(summary = "删除订单", description = "删除指定订单")
    @DeleteMapping("/{id}")
    public RestResult<Void> deleteOrder(@PathVariable String id) {
        orderService.deleteOrder(id);
        return RestResult.success();
    }

    /**
     * 取消订单
     *
     * @param id 订单ID
     * @return 取消结果
     */
    @Operation(summary = "取消订单", description = "取消指定订单")
    @PostMapping("/{id}/cancel")
    public RestResult<Void> cancelOrder(@PathVariable String id) {
        orderService.cancelOrder(id);
        return RestResult.success();
    }

    /**
     * 更新订单支付状态
     *
     * @param id 订单ID
     * @param paymentStatus 支付状态请求
     * @return 更新结果
     */
    @Operation(summary = "更新订单支付状态", description = "更新订单支付状态（paid-已支付，refunded-已退款）")
    @PatchMapping("/{id}/payment-status")
    public RestResult<Void> updateOrderPaymentStatus(@PathVariable String id, 
                                                     @RequestBody PaymentStatusRequest paymentStatus) {
        orderService.updateOrderPaymentStatus(id, paymentStatus.getPaymentStatus());
        return RestResult.success();
    }

    /**
     * 手动检查并更新过期订单
     *
     * @return 执行结果
     */
    @Operation(summary = "检查过期订单", description = "手动触发检查并更新过期订单状态")
    @PostMapping("/check-expired")
    public RestResult<Void> checkExpiredOrders() {
        orderService.checkAndUpdateExpiredOrders();
        return RestResult.success();
    }

    /**
     * 支付状态请求对象
     */
    static class PaymentStatusRequest {
        private String paymentStatus;

        public String getPaymentStatus() {
            return paymentStatus;
        }

        public void setPaymentStatus(String paymentStatus) {
            this.paymentStatus = paymentStatus;
        }
    }
}

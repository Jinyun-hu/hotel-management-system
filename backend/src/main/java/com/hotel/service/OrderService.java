package com.hotel.service;

import com.hotel.common.PageResult;
import com.hotel.dto.OrderDTO;
import com.hotel.query.OrderQuery;

/**
 * 订单服务接口
 */
public interface OrderService {
    
    /**
     * 查询订单列表
     * @param query 查询条件
     * @return 订单列表
     */
    PageResult<OrderDTO> listOrders(OrderQuery query);
    
    /**
     * 新增订单
     * @param orderDTO 订单信息
     * @return 新增的订单
     */
    OrderDTO addOrder(OrderDTO orderDTO);
    
    /**
     * 编辑订单
     * @param id 订单ID
     * @param orderDTO 订单信息
     */
    void updateOrder(String id, OrderDTO orderDTO);
    
    /**
     * 删除订单
     * @param id 订单ID
     */
    void deleteOrder(String id);
    
    /**
     * 取消订单
     * @param id 订单ID
     */
    void cancelOrder(String id);
    
    /**
     * 更新订单支付状态
     * @param id 订单ID
     * @param paymentStatus 支付状态：paid-已支付，refunded-已退款
     */
    void updateOrderPaymentStatus(String id, String paymentStatus);
    
    /**
     * 检查并更新过期订单状态
     * 将退房日期已过的订单状态更新为completed
     */
    void checkAndUpdateExpiredOrders();
}

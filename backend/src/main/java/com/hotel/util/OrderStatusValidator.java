package com.hotel.util;

import com.hotel.common.BusinessException;

/**
 * 订单状态验证器
 * 验证订单状态流转是否合法
 */
public class OrderStatusValidator {

    /**
     * 验证订单状态流转是否合法
     * 
     * 允许的状态流转：
     * 1. pending → active (支付完成)
     * 2. pending → canceled (取消订单)
     * 3. active → completed (入住结束)
     * 4. any → canceled (取消订单)
     * 
     * @param oldStatus 原状态
     * @param newStatus 新状态
     * @throws BusinessException 如果状态流转不合法
     */
    public static void validateStatusTransition(String oldStatus, String newStatus) {
        if (oldStatus == null || newStatus == null) {
            throw new BusinessException("订单状态不能为空");
        }

        // 状态相同，允许
        if (oldStatus.equals(newStatus)) {
            return;
        }

        // 检查是否允许的状态流转
        boolean isValid = false;

        switch (oldStatus) {
            case "pending":
                // pending 可以转为 active 或 canceled
                isValid = "active".equals(newStatus) || "canceled".equals(newStatus);
                break;
            case "active":
                // active 可以转为 completed 或 canceled
                isValid = "completed".equals(newStatus) || "canceled".equals(newStatus);
                break;
            case "completed":
                // completed 是最终状态，不能转为其他状态
                isValid = false;
                break;
            case "canceled":
                // canceled 是最终状态，不能转为其他状态
                isValid = false;
                break;
            default:
                throw new BusinessException("未知的订单状态: " + oldStatus);
        }

        if (!isValid) {
            throw new BusinessException(
                String.format("订单状态流转不合法: 不能从 %s 转为 %s", oldStatus, newStatus)
            );
        }
    }

    /**
     * 验证订单是否可以取消
     * 
     * @param currentStatus 当前状态
     * @return 是否可以取消
     */
    public static boolean canCancel(String currentStatus) {
        // 只有 pending 和 active 状态的订单可以取消
        return "pending".equals(currentStatus) || "active".equals(currentStatus);
    }

    /**
     * 验证订单是否可以完成支付
     * 
     * @param currentStatus 当前状态
     * @return 是否可以完成支付
     */
    public static boolean canPay(String currentStatus) {
        // 只有 pending 状态的订单可以支付
        return "pending".equals(currentStatus);
    }

    /**
     * 验证订单是否可以退款
     * 
     * @param currentStatus 当前状态
     * @param paymentStatus 支付状态
     * @return 是否可以退款
     */
    public static boolean canRefund(String currentStatus, String paymentStatus) {
        // 只有已支付且订单未完成的订单可以退款
        return "paid".equals(paymentStatus) && 
               !"completed".equals(currentStatus) && 
               !"canceled".equals(currentStatus);
    }
}
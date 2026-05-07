package com.hotel.task;

import com.hotel.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 订单过期检查定时任务
 * 每天凌晨1点检查并更新过期订单状态
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderExpirationTask {

    private final OrderService orderService;

    /**
     * 每小时执行一次，检查并更新过期订单
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void checkAndUpdateExpiredOrders() {
        log.info("开始执行过期订单检查任务...");
        
        try {
            orderService.checkAndUpdateExpiredOrders();
            log.info("过期订单检查任务执行完成");
        } catch (Exception e) {
            log.error("过期订单检查任务执行失败", e);
        }
    }
}
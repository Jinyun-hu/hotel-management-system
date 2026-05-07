package com.hotel.task;

import com.hotel.entity.RoomDO;
import com.hotel.entity.OrdersDO;
import com.hotel.mapper.RoomMapper;
import com.hotel.mapper.OrdersMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 房间状态检查定时任务
 * 定期检查房间状态与订单状态的一致性
 */
@Component
@RequiredArgsConstructor
public class RoomStatusCheckTask {

    private final RoomMapper roomMapper;
    private final OrdersMapper ordersMapper;

    /**
     * 每天凌晨2点执行房间状态检查
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void checkRoomStatusConsistency() {
        // 查找所有房间
        List<RoomDO> allRooms = roomMapper.selectList(null);

        for (RoomDO room : allRooms) {
            // 检查该房间是否有正在进行中的订单
            LambdaQueryWrapper<OrdersDO> orderWrapper = new LambdaQueryWrapper<>();
            orderWrapper.eq(OrdersDO::getRoomId, room.getId());
            orderWrapper.in(OrdersDO::getStatus, "active", "pending");
            List<OrdersDO> activeOrders = ordersMapper.selectList(orderWrapper);

            if (!activeOrders.isEmpty()) {
                // 有正在进行中的订单，房间状态应该是已入住
                if (!"occupied".equals(room.getStatus())) {
                    room.setStatus("occupied");
                    roomMapper.updateById(room);
                    System.out.println("房间 " + room.getRoomNumber() + " 状态已更新为已入住，因为有正在进行中的订单");
                }
            } else {
                // 检查是否有已完成但未清洁的订单
                LambdaQueryWrapper<OrdersDO> completedWrapper = new LambdaQueryWrapper<>();
                completedWrapper.eq(OrdersDO::getRoomId, room.getId());
                completedWrapper.eq(OrdersDO::getStatus, "completed");
                List<OrdersDO> completedOrders = ordersMapper.selectList(completedWrapper);

                if (!completedOrders.isEmpty()) {
                    // 有已完成的订单，房间状态应该是等待清洁
                    if (!"waiting_clean".equals(room.getStatus())) {
                        room.setStatus("waiting_clean");
                        roomMapper.updateById(room);
                        System.out.println("房间 " + room.getRoomNumber() + " 状态已更新为等待清洁，因为有已完成的订单");
                    }
                } else {
                    // 没有订单，房间状态应该是空闲
                    if (!"available".equals(room.getStatus()) && !"maintenance".equals(room.getStatus())) {
                        room.setStatus("available");
                        roomMapper.updateById(room);
                        System.out.println("房间 " + room.getRoomNumber() + " 状态已更新为空闲，因为没有相关订单");
                    }
                }
            }
        }
    }
}

package com.hotel.service;

import java.util.Map;

/**
 * 房态可视化服务接口
 */
public interface RoomStatusService {
    
    /**
     * 获取房态数据
     * @param floor 楼层
     * @return 房态数据
     */
    Map<String, Object> getRoomStatus(Integer floor);
    
    /**
     * 更新房间状态
     * @param id 房间ID
     * @param status 房间状态
     * @param cleanStatus 清洁状态
     * @param doNotDisturb 勿扰模式
     */
    void updateRoomStatus(Integer id, String status, String cleanStatus, Boolean doNotDisturb);
}

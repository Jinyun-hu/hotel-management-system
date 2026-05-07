package com.hotel.service;

import com.hotel.common.PageResult;
import com.hotel.dto.RoomDTO;
import com.hotel.entity.OrdersDO;
import com.hotel.query.RoomQuery;

import java.util.List;

/**
 * 房间服务接口
 */
public interface RoomService {
    
    /**
     * 查询房间列表
     * @param query 查询条件
     * @return 房间列表
     */
    PageResult<RoomDTO> listRooms(RoomQuery query);
    
    /**
     * 新增房间
     * @param roomDTO 房间信息
     * @return 新增的房间
     */
    RoomDTO addRoom(RoomDTO roomDTO);
    
    /**
     * 编辑房间
     * @param id 房间ID
     * @param roomDTO 房间信息
     */
    void updateRoom(Integer id, RoomDTO roomDTO);
    
    /**
     * 删除房间
     * @param id 房间ID
     */
    void deleteRoom(Integer id);

    /**
     * 检查是否有订单关联到该房间
     * @param roomId 房间ID
     * @return 关联的订单列表
     */
    List<OrdersDO> checkRoomOrders(Integer roomId);
}

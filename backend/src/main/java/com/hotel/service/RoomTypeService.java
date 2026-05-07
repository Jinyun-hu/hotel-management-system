package com.hotel.service;

import com.hotel.common.PageResult;
import com.hotel.dto.RoomTypeDTO;
import com.hotel.entity.OrdersDO;
import com.hotel.query.RoomTypeQuery;

import java.util.List;

/**
 * 房型服务接口
 */
public interface RoomTypeService {
    
    /**
     * 查询房型列表
     * @param query 查询条件
     * @return 房型列表
     */
    PageResult<RoomTypeDTO> listRoomTypes(RoomTypeQuery query);
    
    /**
     * 新增房型
     * @param roomTypeDTO 房型信息
     * @return 新增的房型
     */
    RoomTypeDTO addRoomType(RoomTypeDTO roomTypeDTO);
    
    /**
     * 编辑房型
     * @param id 房型ID
     * @param roomTypeDTO 房型信息
     */
    void updateRoomType(Integer id, RoomTypeDTO roomTypeDTO);
    
    /**
     * 删除房型
     * @param id 房型ID
     */
    void deleteRoomType(Integer id);

    /**
     * 更新房型状态
     * @param id 房型ID
     * @param status 状态: active-启用, inactive-禁用
     */
    void updateRoomTypeStatus(Integer id, String status);

    /**
     * 检查是否有订单关联到该房型
     * @param roomTypeId 房型ID
     * @return 关联的订单列表
     */
    List<OrdersDO> checkRoomTypeOrders(Integer roomTypeId);
}

package com.hotel.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hotel.common.BusinessException;
import com.hotel.common.ResultCodeConstant;
import com.hotel.dto.RoomDTO;
import com.hotel.dto.RoomStatusDTO;
import com.hotel.entity.RoomDO;
import com.hotel.entity.RoomTypeDO;
import com.hotel.mapper.RoomMapper;
import com.hotel.mapper.RoomTypeMapper;
import com.hotel.service.RoomStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 房态可视化服务实现类
 */
@Service
@RequiredArgsConstructor
public class RoomStatusServiceImpl implements RoomStatusService {

    private final RoomMapper roomMapper;
    private final RoomTypeMapper roomTypeMapper;

    /**
     * 获取房态数据
     */
    @Override
    public Map<String, Object> getRoomStatus(Integer floor) {
        Map<String, Object> result = new HashMap<>();

        LambdaQueryWrapper<RoomDO> wrapper = new LambdaQueryWrapper<>();
        if (floor != null) {
            wrapper.eq(RoomDO::getFloor, floor);
        }
        wrapper.orderByAsc(RoomDO::getFloor)
               .orderByAsc(RoomDO::getRoomNumber);

        List<RoomDO> rooms = roomMapper.selectList(wrapper);

        // 转换为DTO
        List<RoomStatusDTO> roomStatusList = rooms.stream()
                .map(this::convertToStatusDTO)
                .collect(Collectors.toList());

        // 统计数据
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("total", rooms.size());
        statistics.put("available", rooms.stream().filter(r -> "available".equals(r.getStatus())).count());
        statistics.put("occupied", rooms.stream().filter(r -> "occupied".equals(r.getStatus())).count());
        statistics.put("cleaning", rooms.stream().filter(r -> "cleaning".equals(r.getStatus())).count());
        statistics.put("maintenance", rooms.stream().filter(r -> "maintenance".equals(r.getStatus())).count());

        // 按楼层分组
        Map<Integer, List<RoomStatusDTO>> floorMap = rooms.stream()
                .collect(Collectors.groupingBy(
                        RoomDO::getFloor,
                        Collectors.mapping(this::convertToStatusDTO, Collectors.toList())
                ));

        result.put("rooms", roomStatusList);
        result.put("statistics", statistics);
        result.put("floors", floorMap);

        return result;
    }

    /**
     * 更新房间状态
     */
    @Override
    public void updateRoomStatus(Integer id, String status, String cleanStatus, Boolean doNotDisturb) {
        RoomDO existing = roomMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCodeConstant.ROOM_NOT_FOUND);
        }

        if (status != null && !status.isEmpty()) {
            existing.setStatus(status);
        }

        if (cleanStatus != null && !cleanStatus.isEmpty()) {
            existing.setCleanStatus(cleanStatus);
        }

        if (doNotDisturb != null) {
            existing.setDoNotDisturb(doNotDisturb);
        }

        roomMapper.updateById(existing);
    }

    private RoomStatusDTO convertToStatusDTO(RoomDO entity) {
        RoomStatusDTO dto = BeanUtil.copyProperties(entity, RoomStatusDTO.class);
        // 获取房型信息
        if (entity.getRoomTypeId() != null) {
            RoomTypeDO roomType = roomTypeMapper.selectById(entity.getRoomTypeId());
            if (roomType != null) {
                dto.setRoomTypeName(roomType.getName());
                dto.setCapacity(roomType.getCapacity());
            }
        }
        return dto;
    }
}

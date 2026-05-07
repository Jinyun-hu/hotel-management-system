package com.hotel.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.common.BusinessException;
import com.hotel.common.PageResult;
import com.hotel.common.ResultCodeConstant;
import com.hotel.dto.RoomDTO;
import com.hotel.entity.OrdersDO;
import com.hotel.entity.RoomDO;
import com.hotel.entity.RoomTypeDO;
import com.hotel.mapper.OrdersMapper;
import com.hotel.mapper.RoomMapper;
import com.hotel.mapper.RoomTypeMapper;
import com.hotel.query.RoomQuery;
import com.hotel.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
/**
 * 房间服务实现类
 */
@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomMapper roomMapper;
    private final RoomTypeMapper roomTypeMapper;
    private final OrdersMapper ordersMapper;

    /**
     * 查询房间列表
     */
    @Override
    public PageResult<RoomDTO> listRooms(RoomQuery query) {
        LambdaQueryWrapper<RoomDO> wrapper = new LambdaQueryWrapper<>();

        if (query != null) {
            // 处理前端发送的 keyword 参数，对应后端的 roomNumber 字段
            if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
                wrapper.like(RoomDO::getRoomNumber, query.getKeyword());
            }
            // 处理前端发送的 roomNumber 参数
            if (query.getRoomNumber() != null && !query.getRoomNumber().isEmpty()) {
                wrapper.like(RoomDO::getRoomNumber, query.getRoomNumber());
            }
            // 处理前端发送的 type 参数，对应后端的 roomTypeId 字段
            if (query.getRoomTypeId() != null) {
                wrapper.eq(RoomDO::getRoomTypeId, query.getRoomTypeId());
            }
            // 处理前端发送的 type 参数
            if (query.getType() != null) {
                wrapper.eq(RoomDO::getRoomTypeId, query.getType());
            }
            if (query.getFloor() != null) {
                wrapper.eq(RoomDO::getFloor, query.getFloor());
            }
            if (query.getStatus() != null && !query.getStatus().isEmpty()) {
                wrapper.eq(RoomDO::getStatus, query.getStatus());
            }
        }

        wrapper.orderByAsc(RoomDO::getFloor)
               .orderByAsc(RoomDO::getRoomNumber);

        // 分页查询
        int pageNum = query != null && query.getPage() != null ? query.getPage() : 1;
        int pageSize = query != null && query.getSize() != null ? query.getSize() : 10;

        Page<RoomDO> page = new Page<>(pageNum, pageSize);
        Page<RoomDO> result = roomMapper.selectPage(page, wrapper);

        List<RoomDTO> roomDTOs = result.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return PageResult.of(roomDTOs, result.getTotal(), pageNum, pageSize);
    }


    /**
     * 新增房间
     */
    @Override
    public RoomDTO addRoom(RoomDTO roomDTO) {
        // 检查房间编号是否已存在
        LambdaQueryWrapper<RoomDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoomDO::getRoomNumber, roomDTO.getRoomNumber());
        RoomDO existing = roomMapper.selectOne(wrapper);
        if (existing != null) {
            throw new BusinessException(ResultCodeConstant.ROOM_ALREADY_EXISTS);
        }

        // 验证房型是否存在
        RoomTypeDO roomType = roomTypeMapper.selectById(roomDTO.getRoomTypeId());
        if (roomType == null) {
            throw new BusinessException(ResultCodeConstant.ROOM_TYPE_NOT_FOUND);
        }

        RoomDO roomDO = convertToEntity(roomDTO);
        roomMapper.insert(roomDO);
        return convertToDTO(roomDO);
    }

    /**
     * 编辑房间
     */
    @Override
    public void updateRoom(Integer id, RoomDTO roomDTO) {
        RoomDO existing = roomMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCodeConstant.ROOM_NOT_FOUND);
        }

        // 检查房间编号是否与其他房间重复
        LambdaQueryWrapper<RoomDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoomDO::getRoomNumber, roomDTO.getRoomNumber());
        wrapper.ne(RoomDO::getId, id);
        RoomDO duplicate = roomMapper.selectOne(wrapper);
        if (duplicate != null) {
            throw new BusinessException(ResultCodeConstant.ROOM_ALREADY_EXISTS);
        }

        // 验证房型是否存在
        if (roomDTO.getRoomTypeId() != null) {
            RoomTypeDO roomType = roomTypeMapper.selectById(roomDTO.getRoomTypeId());
            if (roomType == null) {
                throw new BusinessException(ResultCodeConstant.ROOM_TYPE_NOT_FOUND);
            }
        }

        // 状态修改校验
        if (roomDTO.getStatus() != null && !roomDTO.getStatus().equals(existing.getStatus())) {
            String oldStatus = existing.getStatus();
            String newStatus = roomDTO.getStatus();

            // 1. 检查是否有进行中的订单
            LambdaQueryWrapper<OrdersDO> activeOrderWrapper = new LambdaQueryWrapper<>();
            activeOrderWrapper.eq(OrdersDO::getRoomId, id)
                    .eq(OrdersDO::getStatus, "active");
            List<OrdersDO> activeOrders = ordersMapper.selectList(activeOrderWrapper);
            if (!activeOrders.isEmpty()) {
                throw new BusinessException("当前房间有客人正在入住，无法修改状态，请先办理退房");
            }

            // 2. 检查是否有订单关联到该房间（用于判断是否可以改为已入住）
            List<OrdersDO> relatedOrders = checkRoomOrders(id);
            
            // 3. 检查是否可以改为已入住状态
            if ("occupied".equals(newStatus)) {
                if (relatedOrders.isEmpty()) {
                    throw new BusinessException("没有订单关联到该房间，无法设置为已入住状态");
                }
                // 检查是否有有效的订单（进行中或待处理）
                boolean hasValidOrder = false;
                for (OrdersDO order : relatedOrders) {
                    if ("active".equals(order.getStatus()) || "pending".equals(order.getStatus())) {
                        hasValidOrder = true;
                        break;
                    }
                }
                if (!hasValidOrder) {
                    throw new BusinessException("没有有效的订单，无法设置为已入住状态");
                }
            }

            // 4. 状态流转限制
            validateStatusTransition(oldStatus, newStatus);
        }

        RoomDO roomDO = convertToEntity(roomDTO);
        roomDO.setId(id);
        roomMapper.updateById(roomDO);
    }

    /**
     * 验证房间状态流转是否合法
     */
    private void validateStatusTransition(String oldStatus, String newStatus) {
        // 禁止「已入住」→「空闲」（必须先退房→等待清洁/清洁中→空闲）
        if ("occupied".equals(oldStatus) && "available".equals(newStatus)) {
            throw new BusinessException("不允许直接从已入住改为空闲，必须先退房→等待清洁/清洁中→空闲");
        }
        // 禁止「维修中」→「已入住」（必须先改为空闲，再办理入住）
        if ("maintenance".equals(oldStatus) && "occupied".equals(newStatus)) {
            throw new BusinessException("不允许直接从维修中改为已入住，必须先改为空闲，再办理入住");
        }
        // 禁止「清洁中」→「已入住」（必须先改为空闲，再办理入住）
        if ("cleaning".equals(oldStatus) && "occupied".equals(newStatus)) {
            throw new BusinessException("不允许直接从清洁中改为已入住，必须先改为空闲，再办理入住");
        }
        // 禁止「等待清洁」→「已入住」（必须先改为清洁中→空闲，再办理入住）
        if ("waiting_clean".equals(oldStatus) && "occupied".equals(newStatus)) {
            throw new BusinessException("不允许直接从等待清洁改为已入住，必须先改为清洁中→空闲，再办理入住");
        }
    }

    /**
     * 删除房间
     */
    @Override
    public void deleteRoom(Integer id) {
        RoomDO existing = roomMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCodeConstant.ROOM_NOT_FOUND);
        }

        // 检查是否有订单关联到该房间
        List<OrdersDO> relatedOrders = checkRoomOrders(id);
        if (relatedOrders != null && !relatedOrders.isEmpty()) {
            throw new BusinessException("该房间下存在关联的订单，无法删除");
        }

        roomMapper.deleteById(id);
    }

    /**
     * 检查是否有订单关联到该房间
     */
    public List<OrdersDO> checkRoomOrders(Integer roomId) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<OrdersDO> orderWrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        orderWrapper.eq(OrdersDO::getRoomId, roomId);
        return ordersMapper.selectList(orderWrapper);
    }

    private RoomDTO convertToDTO(RoomDO entity) {
        RoomDTO dto = BeanUtil.copyProperties(entity, RoomDTO.class);
        // 获取房型名称
        if (entity.getRoomTypeId() != null) {
            RoomTypeDO roomType = roomTypeMapper.selectById(entity.getRoomTypeId());
            if (roomType != null) {
                dto.setRoomTypeName(roomType.getName());
            }
        }
        // 转换时间格式
        if (entity.getCreateTime() != null) {
            dto.setCreateTime(entity.getCreateTime().toString());
        }
        if (entity.getUpdateTime() != null) {
            dto.setUpdateTime(entity.getUpdateTime().toString());
        }
        return dto;
    }

    private RoomDO convertToEntity(RoomDTO dto) {
        RoomDO roomDO = BeanUtil.copyProperties(dto, RoomDO.class);
        // DTO中的时间是字符串，实体类中不设置时间字段
        // 由MyBatis-Plus自动填充
        roomDO.setCreateTime(null);
        roomDO.setUpdateTime(null);
        return roomDO;
    }
}

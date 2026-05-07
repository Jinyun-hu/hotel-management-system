package com.hotel.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.common.BusinessException;
import com.hotel.common.PageResult;
import com.hotel.common.ResultCodeConstant;
import com.hotel.dto.RoomTypeDTO;
import com.hotel.entity.OrdersDO;
import com.hotel.entity.RoomTypeDO;
import com.hotel.mapper.OrdersMapper;
import com.hotel.mapper.RoomTypeMapper;
import com.hotel.query.RoomTypeQuery;
import com.hotel.service.RoomTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 房型服务实现类
 */
@Service
@RequiredArgsConstructor
public class RoomTypeServiceImpl implements RoomTypeService {

    private final RoomTypeMapper roomTypeMapper;
    private final OrdersMapper ordersMapper;

    /**
     * 查询房型列表
     */
    @Override
    public PageResult<RoomTypeDTO> listRoomTypes(RoomTypeQuery query) {
        LambdaQueryWrapper<RoomTypeDO> wrapper = new LambdaQueryWrapper<>();

        if (query != null) {
            if (query.getName() != null && !query.getName().isEmpty()) {
                wrapper.like(RoomTypeDO::getName, query.getName());
            }
            if (query.getStatus() != null && !query.getStatus().isEmpty()) {
                wrapper.eq(RoomTypeDO::getStatus, query.getStatus());
            }
        }

        wrapper.orderByDesc(RoomTypeDO::getCreateTime);

        // 处理分页参数,避免 null 指针异常
        Integer pageNum = (query != null && query.getPage() != null) ? query.getPage() : 1;
        Integer pageSize = (query != null && query.getSize() != null) ? query.getSize() : 10;

        Page<RoomTypeDO> page = new Page<>(pageNum, pageSize);
        Page<RoomTypeDO> result = roomTypeMapper.selectPage(page, wrapper);

        List<RoomTypeDTO> records = result.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return PageResult.of(records, result.getTotal(), pageNum, pageSize);
    }

    /**
     * 新增房型
     */
    @Override
    public RoomTypeDTO addRoomType(RoomTypeDTO roomTypeDTO) {
        // 检查房型名称是否已存在
        LambdaQueryWrapper<RoomTypeDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoomTypeDO::getName, roomTypeDTO.getName());
        RoomTypeDO existing = roomTypeMapper.selectOne(wrapper);
        if (existing != null) {
            throw new BusinessException(ResultCodeConstant.ROOM_TYPE_ALREADY_EXISTS);
        }

        RoomTypeDO roomTypeDO = convertToEntity(roomTypeDTO);
        roomTypeMapper.insert(roomTypeDO);
        return convertToDTO(roomTypeDO);
    }

    /**
     * 编辑房型
     */
    @Override
    public void updateRoomType(Integer id, RoomTypeDTO roomTypeDTO) {
        RoomTypeDO existing = roomTypeMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCodeConstant.ROOM_TYPE_NOT_FOUND);
        }

        // 检查名称是否与其他房型重复
        LambdaQueryWrapper<RoomTypeDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoomTypeDO::getName, roomTypeDTO.getName());
        wrapper.ne(RoomTypeDO::getId, id);
        RoomTypeDO duplicate = roomTypeMapper.selectOne(wrapper);
        if (duplicate != null) {
            throw new BusinessException(ResultCodeConstant.ROOM_TYPE_ALREADY_EXISTS);
        }

        // 如果要将房型状态从active改为inactive，检查是否有未完成的订单
        if ("active".equals(existing.getStatus()) && "inactive".equals(roomTypeDTO.getStatus())) {
            List<OrdersDO> relatedOrders = checkRoomTypeOrders(id);
            if (relatedOrders != null && !relatedOrders.isEmpty()) {
                // 检查是否有未完成的订单（pending、active状态）
                List<OrdersDO> activeOrders = relatedOrders.stream()
                        .filter(order -> "pending".equals(order.getStatus()) || "active".equals(order.getStatus()))
                        .toList();
                
                if (!activeOrders.isEmpty()) {
                    long pendingCount = activeOrders.stream().filter(o -> "pending".equals(o.getStatus())).count();
                    long activeCount = activeOrders.stream().filter(o -> "active".equals(o.getStatus())).count();
                    throw new BusinessException(String.format("该房型下存在未完成的订单，无法停用（待入住：%d个，入住中：%d个）", pendingCount, activeCount));
                }
            }
        }

        RoomTypeDO roomTypeDO = convertToEntity(roomTypeDTO);
        roomTypeDO.setId(id);
        roomTypeMapper.updateById(roomTypeDO);
    }

    /**
     * 删除房型
     */
    @Override
    public void deleteRoomType(Integer id) {
        RoomTypeDO existing = roomTypeMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCodeConstant.ROOM_TYPE_NOT_FOUND);
        }

        // 检查是否有订单关联到该房型
        List<OrdersDO> relatedOrders = checkRoomTypeOrders(id);
        if (relatedOrders != null && !relatedOrders.isEmpty()) {
            throw new BusinessException("该房型下存在关联的订单，无法删除");
        }

        roomTypeMapper.deleteById(id);
    }

    /**
     * 更新房型状态
     */
    @Override
    public void updateRoomTypeStatus(Integer id, String status) {
        RoomTypeDO existing = roomTypeMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCodeConstant.ROOM_TYPE_NOT_FOUND);
        }

        // 如果要停用房型，检查是否有订单关联
        if ("inactive".equals(status)) {
            List<OrdersDO> relatedOrders = checkRoomTypeOrders(id);
            if (relatedOrders != null && !relatedOrders.isEmpty()) {
                // 检查是否有未完成的订单（pending、active状态）
                List<OrdersDO> activeOrders = relatedOrders.stream()
                        .filter(order -> "pending".equals(order.getStatus()) || "active".equals(order.getStatus()))
                        .toList();
                
                if (!activeOrders.isEmpty()) {
                    long pendingCount = activeOrders.stream().filter(o -> "pending".equals(o.getStatus())).count();
                    long activeCount = activeOrders.stream().filter(o -> "active".equals(o.getStatus())).count();
                    throw new BusinessException(String.format("该房型下存在未完成的订单，无法停用（待入住：%d个，入住中：%d个）", pendingCount, activeCount));
                }
                
                // 如果有已完成或已取消的订单，给出提示但允许停用
                // 可以添加日志记录或通知
            }
        }

        existing.setStatus(status);
        roomTypeMapper.updateById(existing);
    }

    /**
     * 检查是否有订单关联到该房型
     */
    public List<OrdersDO> checkRoomTypeOrders(Integer roomTypeId) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<OrdersDO> orderWrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        orderWrapper.eq(OrdersDO::getRoomTypeId, roomTypeId);
        return ordersMapper.selectList(orderWrapper);
    }

    private RoomTypeDTO convertToDTO(RoomTypeDO entity) {
        return BeanUtil.copyProperties(entity, RoomTypeDTO.class);
    }

    private RoomTypeDO convertToEntity(RoomTypeDTO dto) {
        return BeanUtil.copyProperties(dto, RoomTypeDO.class);
    }
}

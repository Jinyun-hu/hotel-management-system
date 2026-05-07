package com.hotel.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.common.BusinessException;
import com.hotel.common.PageResult;
import com.hotel.common.ResultCodeConstant;
import com.hotel.dto.OrderDTO;
import com.hotel.entity.OrdersDO;
import com.hotel.entity.RoomDO;
import com.hotel.entity.RoomTypeDO;
import com.hotel.mapper.OrdersMapper;
import com.hotel.mapper.RoomMapper;
import com.hotel.mapper.RoomTypeMapper;
import com.hotel.query.OrderQuery;
import com.hotel.service.OrderService;
import com.hotel.util.OrderStatusValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单服务实现类
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrdersMapper ordersMapper;
    private final RoomMapper roomMapper;
    private final RoomTypeMapper roomTypeMapper;

    /**
     * 查询订单列表
     */
    @Override
    public PageResult<OrderDTO> listOrders(OrderQuery query) {
        LambdaQueryWrapper<OrdersDO> wrapper = new LambdaQueryWrapper<>();

        if (query != null) {
            if (query.getOrderNo() != null && !query.getOrderNo().isEmpty()) {
                wrapper.like(OrdersDO::getOrderNo, query.getOrderNo());
            }
            if (query.getGuestName() != null && !query.getGuestName().isEmpty()) {
                wrapper.like(OrdersDO::getGuestName, query.getGuestName());
            }
            if (query.getGuestPhone() != null && !query.getGuestPhone().isEmpty()) {
                wrapper.like(OrdersDO::getGuestPhone, query.getGuestPhone());
            }
            if (query.getRoomId() != null) {
                wrapper.eq(OrdersDO::getRoomId, query.getRoomId());
            }
            if (query.getRoomNumber() != null && !query.getRoomNumber().isEmpty()) {
                // 通过房间编号查询房间ID
                LambdaQueryWrapper<RoomDO> roomWrapper = new LambdaQueryWrapper<>();
                roomWrapper.eq(RoomDO::getRoomNumber, query.getRoomNumber());
                RoomDO room = roomMapper.selectOne(roomWrapper);
                if (room != null) {
                    wrapper.eq(OrdersDO::getRoomId, room.getId());
                } else {
                    // 如果找不到房间，返回空结果
                    wrapper.eq(OrdersDO::getId, -1);
                }
            }
            if (query.getRoomTypeId() != null) {
                wrapper.eq(OrdersDO::getRoomTypeId, query.getRoomTypeId());
            }
            if (query.getStatus() != null && !query.getStatus().isEmpty()) {
                wrapper.eq(OrdersDO::getStatus, query.getStatus());
            }
            if (query.getPaymentStatus() != null && !query.getPaymentStatus().isEmpty()) {
                wrapper.eq(OrdersDO::getPaymentStatus, query.getPaymentStatus());
            }
            if (query.getCheckInDate() != null) {
                wrapper.ge(OrdersDO::getCheckInDate, query.getCheckInDate());
            }
            if (query.getCheckOutDate() != null) {
                wrapper.le(OrdersDO::getCheckOutDate, query.getCheckOutDate());
            }
        }

        wrapper.orderByDesc(OrdersDO::getCreateTime);

        // 分页查询
        int pageNum = query != null && query.getPage() != null ? query.getPage() : 1;
        int pageSize = query != null && query.getSize() != null ? query.getSize() : 10;

        Page<OrdersDO> page = new Page<>(pageNum, pageSize);
        Page<OrdersDO> result = ordersMapper.selectPage(page, wrapper);

        List<OrderDTO> records = result.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return PageResult.of(records, result.getTotal(), pageNum, pageSize);
    }

    /**
     * 新增订单
     */
    @Override
    @Transactional
    public OrderDTO addOrder(OrderDTO orderDTO) {
        // 验证房间是否存在且可用
        RoomDO room = roomMapper.selectById(orderDTO.getRoomId());
        if (room == null) {
            throw new BusinessException(ResultCodeConstant.ROOM_NOT_FOUND);
        }
        if (!"available".equals(room.getStatus())) {
            throw new BusinessException(ResultCodeConstant.ROOM_NOT_AVAILABLE);
        }

        // 验证房型是否存在
        RoomTypeDO roomType = roomTypeMapper.selectById(orderDTO.getRoomTypeId());
        if (roomType == null) {
            throw new BusinessException(ResultCodeConstant.ROOM_TYPE_NOT_FOUND);
        }

        // 计算入住天数
        long nights = ChronoUnit.DAYS.between(
                LocalDate.parse(orderDTO.getCheckInDate()),
                LocalDate.parse(orderDTO.getCheckOutDate()));
        if (nights <= 0) {
            throw new BusinessException("入住日期必须早于退房日期");
        }

        // 生成订单编号
        String orderNo = "ORD" + System.currentTimeMillis();

        // 自动计算订单金额
        BigDecimal totalAmount = roomType.getPrice()
                .multiply(BigDecimal.valueOf(nights))
                .setScale(2, RoundingMode.HALF_UP);

        // 创建订单
        OrdersDO ordersDO = convertToEntity(orderDTO);
        ordersDO.setOrderNo(orderNo);
        ordersDO.setNights((int) nights);
        ordersDO.setTotalAmount(totalAmount);
        // 如果没有身份证号,设置为空字符串或null
        if (ordersDO.getGuestIdCard() == null) {
            ordersDO.setGuestIdCard(null);
        }
        ordersMapper.insert(ordersDO);

        // 更新房间状态
        room.setStatus("occupied");
        roomMapper.updateById(room);

        return convertToDTO(ordersDO);
    }

    /**
     * 编辑订单
     */
    @Override
    @Transactional
    public void updateOrder(String id, OrderDTO orderDTO) {
        OrdersDO existing = ordersMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCodeConstant.ORDER_NOT_FOUND);
        }

        // 验证房间是否存在
        RoomDO room = roomMapper.selectById(orderDTO.getRoomId());
        if (room == null) {
            throw new BusinessException(ResultCodeConstant.ROOM_NOT_FOUND);
        }

        // 验证房型是否存在
        RoomTypeDO roomType = roomTypeMapper.selectById(orderDTO.getRoomTypeId());
        if (roomType == null) {
            throw new BusinessException(ResultCodeConstant.ROOM_TYPE_NOT_FOUND);
        }

        // 计算入住天数
        long nights = ChronoUnit.DAYS.between(
                LocalDate.parse(orderDTO.getCheckInDate()),
                LocalDate.parse(orderDTO.getCheckOutDate()));
        if (nights <= 0) {
            throw new BusinessException("入住日期必须早于退房日期");
        }

        // 验证订单状态流转是否合法
        if (orderDTO.getStatus() != null && !orderDTO.getStatus().equals(existing.getStatus())) {
            // 检查是否是从 pending 转为 completed 且退房日期已过
            LocalDate today = LocalDate.now();
            LocalDate checkOutDate = existing.getCheckOutDate();
            boolean isCheckOutDatePassed = checkOutDate.isBefore(today);
            
            if (!"pending".equals(existing.getStatus()) || !"completed".equals(orderDTO.getStatus()) || !isCheckOutDatePassed) {
                OrderStatusValidator.validateStatusTransition(existing.getStatus(), orderDTO.getStatus());
            }
        }

        // 验证订单状态和支付状态的关联关系
        validateStatusAndPaymentStatus(orderDTO.getStatus(), orderDTO.getPaymentStatus(), existing);

        // 自动计算订单金额
        BigDecimal totalAmount = roomType.getPrice()
                .multiply(BigDecimal.valueOf(nights))
                .setScale(2, RoundingMode.HALF_UP);

        OrdersDO ordersDO = convertToEntity(orderDTO);
        ordersDO.setId(Integer.valueOf(id));
        ordersDO.setNights((int) nights);
        ordersDO.setTotalAmount(totalAmount);
        ordersMapper.updateById(ordersDO);

        // 状态流转时的房间状态同步
        if (orderDTO.getStatus() != null && !orderDTO.getStatus().equals(existing.getStatus())) {
            if ("active".equals(orderDTO.getStatus()) && "pending".equals(existing.getStatus())) {
                // 订单从待处理→进行中，房间状态更新为已入住
                room.setStatus("occupied");
                roomMapper.updateById(room);
            } else if ("completed".equals(orderDTO.getStatus())) {
                // 订单完成，无论之前是什么状态，都将房间状态更新为等待清洁
                room.setStatus("waiting_clean");
                roomMapper.updateById(room);
            } else if ("canceled".equals(orderDTO.getStatus())) {
                // 订单取消，释放房间
                room.setStatus("available");
                roomMapper.updateById(room);
            }
        }
    }

    /**
     * 删除订单
     */
    @Override
    @Transactional
    public void deleteOrder(String id) {
        OrdersDO existing = ordersMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCodeConstant.ORDER_NOT_FOUND);
        }

        ordersMapper.deleteById(id);

        // 释放房间
        if (existing.getRoomId() != null && "active".equals(existing.getStatus())) {
            RoomDO room = roomMapper.selectById(existing.getRoomId());
            if (room != null) {
                room.setStatus("available");
                roomMapper.updateById(room);
            }
        }
    }

    /**
     * 取消订单
     */
    @Override
    @Transactional
    public void cancelOrder(String id) {
        OrdersDO existing = ordersMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCodeConstant.ORDER_NOT_FOUND);
        }

        // 验证是否可以取消
        if (!OrderStatusValidator.canCancel(existing.getStatus())) {
            throw new BusinessException("当前订单状态不允许取消");
        }

        // 验证状态流转是否合法
        OrderStatusValidator.validateStatusTransition(existing.getStatus(), "canceled");

        // 更新订单状态
        existing.setStatus("canceled");
        ordersMapper.updateById(existing);

        // 释放房间
        if (existing.getRoomId() != null) {
            RoomDO room = roomMapper.selectById(existing.getRoomId());
            if (room != null) {
                // 若原订单状态为「待处理」，自动恢复房间状态为「空闲」
                // 注意：这里简化处理，实际应该记录房间原状态
                room.setStatus("available");
                roomMapper.updateById(room);
            }
        }
    }

    /**
     * 更新订单支付状态
     */
    @Override
    @Transactional
    public void updateOrderPaymentStatus(String id, String paymentStatus) {
        OrdersDO existing = ordersMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCodeConstant.ORDER_NOT_FOUND);
        }

        // 验证支付状态是否有效
        if (!"paid".equals(paymentStatus) && !"refunded".equals(paymentStatus)) {
            throw new BusinessException("无效的支付状态，必须是 'paid' 或 'refunded'");
        }

        // 验证支付操作是否允许
        if ("paid".equals(paymentStatus)) {
            // 验证是否可以支付
            if (!OrderStatusValidator.canPay(existing.getStatus())) {
                throw new BusinessException("当前订单状态不允许支付");
            }
        } else if ("refunded".equals(paymentStatus)) {
            // 验证是否可以退款
            if (!OrderStatusValidator.canRefund(existing.getStatus(), existing.getPaymentStatus())) {
                throw new BusinessException("当前订单状态或支付状态不允许退款");
            }
        }

        // 更新支付状态
        existing.setPaymentStatus(paymentStatus);
        
        // 如果订单状态是 pending 且支付成功，更新为 active
        if ("pending".equals(existing.getStatus()) && "paid".equals(paymentStatus)) {
            existing.setStatus("active");
        }
        
        // 注意：退款完成不自动改变订单状态，因为可能有部分退款
        // 如果需要取消订单，应该调用 cancelOrder 方法
        
        ordersMapper.updateById(existing);
    }

    /**
     * 检查并更新过期订单状态
     */
    @Override
    @Transactional
    public void checkAndUpdateExpiredOrders() {
        LocalDate today = LocalDate.now();
        
        // 查询所有未完成的订单（pending 或 active 状态）
        LambdaQueryWrapper<OrdersDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(OrdersDO::getStatus, "pending", "active")
               .lt(OrdersDO::getCheckOutDate, today);
        
        List<OrdersDO> expiredOrders = ordersMapper.selectList(wrapper);
        
        if (!expiredOrders.isEmpty()) {
            for (OrdersDO order : expiredOrders) {
                // 将过期订单状态更新为 completed
                order.setStatus("completed");
                ordersMapper.updateById(order);
                
                // 释放房间
                if (order.getRoomId() != null) {
                    RoomDO room = roomMapper.selectById(order.getRoomId());
                    if (room != null) {
                        room.setStatus("waiting_clean");
                        roomMapper.updateById(room);
                    }
                }
            }
        }
    }

    private OrderDTO convertToDTO(OrdersDO entity) {
        OrderDTO dto = BeanUtil.copyProperties(entity, OrderDTO.class);
        // 获取房间编号
        if (entity.getRoomId() != null) {
            RoomDO room = roomMapper.selectById(entity.getRoomId());
            if (room != null) {
                dto.setRoomNumber(room.getRoomNumber());
            }
        }
        // 获取房型名称
        if (entity.getRoomTypeId() != null) {
            RoomTypeDO roomType = roomTypeMapper.selectById(entity.getRoomTypeId());
            if (roomType != null) {
                dto.setRoomTypeName(roomType.getName());
            }
        }
        return dto;
    }

    /**
     * 验证订单状态和支付状态的关联关系
     */
    private void validateStatusAndPaymentStatus(String status, String paymentStatus, OrdersDO existing) {
        if (status == null) {
            status = existing.getStatus();
        }
        if (paymentStatus == null) {
            paymentStatus = existing.getPaymentStatus();
        }

        // 订单状态「已取消」时
        if ("canceled".equals(status)) {
            // 不允许支付状态为「已支付」
            if ("paid".equals(paymentStatus)) {
                throw new BusinessException("订单状态为已取消时，不允许支付状态为已支付");
            }
            // 必须是「未支付」或「已退款」
            if (!"unpaid".equals(paymentStatus) && !"refunded".equals(paymentStatus)) {
                throw new BusinessException("订单状态为已取消时，支付状态必须是未支付或已退款");
            }
            // 若原订单是已支付状态，取消时必须强制把支付状态更新为「已退款」
            if ("paid".equals(existing.getPaymentStatus())) {
                existing.setPaymentStatus("refunded");
                ordersMapper.updateById(existing);
            }
        }
        // 订单状态「已完成」时
        else if ("completed".equals(status)) {
            // 不允许支付状态为「已退款」
            if ("refunded".equals(paymentStatus)) {
                throw new BusinessException("订单状态为已完成时，不允许支付状态为已退款");
            }
            // 只能是「已支付」或「未支付（挂账）」
            if (!"paid".equals(paymentStatus) && !"unpaid".equals(paymentStatus)) {
                throw new BusinessException("订单状态为已完成时，支付状态只能是已支付或未支付（挂账）");
            }
        }
        // 订单状态「进行中」时
        else if ("active".equals(status)) {
            // 不允许支付状态为「已退款」
            if ("refunded".equals(paymentStatus)) {
                throw new BusinessException("订单状态为进行中时，不允许支付状态为已退款");
            }
            // 支持「未支付 / 已支付」，对应到店付、已预付的场景
            if (!"unpaid".equals(paymentStatus) && !"paid".equals(paymentStatus)) {
                throw new BusinessException("订单状态为进行中时，支付状态只能是未支付或已支付");
            }
        }
        // 订单状态「待处理」时
        else if ("pending".equals(status)) {
            // 不允许支付状态为「已退款」
            if ("refunded".equals(paymentStatus)) {
                throw new BusinessException("订单状态为待处理时，不允许支付状态为已退款");
            }
            // 支持「未支付 / 已支付」，对应未付款预订、已预付预订
            if (!"unpaid".equals(paymentStatus) && !"paid".equals(paymentStatus)) {
                throw new BusinessException("订单状态为待处理时，支付状态只能是未支付或已支付");
            }
        }
    }

    private OrdersDO convertToEntity(OrderDTO dto) {
        return BeanUtil.copyProperties(dto, OrdersDO.class);
    }
}

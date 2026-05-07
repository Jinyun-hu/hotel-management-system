package com.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotel.entity.OrdersDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单Mapper接口
 */
@Mapper
public interface OrdersMapper extends BaseMapper<OrdersDO> {
}

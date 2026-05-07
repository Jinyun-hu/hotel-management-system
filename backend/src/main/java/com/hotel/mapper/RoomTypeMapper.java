package com.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotel.entity.RoomTypeDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 房型Mapper接口
 */
@Mapper
public interface RoomTypeMapper extends BaseMapper<RoomTypeDO> {
}

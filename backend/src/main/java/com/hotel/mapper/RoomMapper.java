package com.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotel.entity.RoomDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 房间Mapper接口
 */
@Mapper
public interface RoomMapper extends BaseMapper<RoomDO> {
}

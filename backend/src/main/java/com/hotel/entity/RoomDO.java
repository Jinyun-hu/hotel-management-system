package com.hotel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 房间实体类
 */
@Data
@Accessors(chain = true)
@TableName("room")
public class RoomDO {

    /**
     * 房间ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 房间编号
     */
    private String roomNumber;

    /**
     * 所属房型ID
     */
    private Integer roomTypeId;

    /**
     * 所在楼层
     */
    private Integer floor;

    /**
     * 房间状态：available-空闲，occupied-已入住，cleaning-清洁中，maintenance-维修中，waiting_clean-等待清洁
     */
    private String status;

    /**
     * 清洁状态：clean-已清洁，dirty-脏，cleaning-清洁中
     */
    private String cleanStatus;

    /**
     * 勿扰模式：true-开启，false-关闭
     */
    private Boolean doNotDisturb;

    /**
     * 房间价格
     */
    private BigDecimal price;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

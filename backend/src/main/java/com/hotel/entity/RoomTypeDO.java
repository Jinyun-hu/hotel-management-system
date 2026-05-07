package com.hotel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 房型实体类
 */
@Data
@Accessors(chain = true)
@TableName("room_type")
public class RoomTypeDO {

    /**
     * 房型ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 房型名称
     */
    private String name;

    /**
     * 可住人数
     */
    private Integer capacity;

    /**
     * 床位描述
     */
    private String beds;

    /**
     * 参考价格
     */
    private BigDecimal price;

    /**
     * 状态：active-启用，inactive-禁用
     */
    private String status;

    /**
     * 房型封面图片URL
     */
    private String image;

    /**
     * 房型描述
     */
    private String description;

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

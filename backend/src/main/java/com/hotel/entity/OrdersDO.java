package com.hotel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 订单实体类
 */
@Data
@Accessors(chain = true)
@TableName("orders")
public class OrdersDO {

    /**
     * 订单ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 客人姓名
     */
    private String guestName;

    /**
     * 客人电话
     */
    private String guestPhone;

    /**
     * 客人身份证号
     */
    private String guestIdCard;

    /**
     * 入住人数
     */
    private Integer guestCount;

    /**
     * 预订房间ID
     */
    private Integer roomId;

    /**
     * 预订房型ID
     */
    private Integer roomTypeId;

    /**
     * 入住日期
     */
    private LocalDate checkInDate;

    /**
     * 退房日期
     */
    private LocalDate checkOutDate;

    /**
     * 入住天数
     */
    private Integer nights;

    /**
     * 订单状态：pending-待入住，active-入住中，completed-已完成，canceled-已取消
     */
    private String status;

    /**
     * 支付状态：unpaid-未支付，paid-已支付，refunded-已退款
     */
    private String paymentStatus;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 备注
     */
    private String remark;

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

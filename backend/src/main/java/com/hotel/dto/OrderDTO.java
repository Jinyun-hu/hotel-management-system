package com.hotel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 订单DTO
 */
@Data
@Schema(name = "订单DTO")
public class OrderDTO {
    
    /**
     * 订单ID
     */
    @Schema(description = "订单ID")
    private Integer id;

    /**
     * 订单编号
     */
    @Schema(description = "订单编号")
    private String orderNo;

    /**
     * 客人姓名
     */
    @Schema(description = "客人姓名")
    private String guestName;

    /**
     * 客人电话
     */
    @Schema(description = "客人电话")
    private String guestPhone;

    /**
     * 客人身份证号
     */
    @Schema(description = "客人身份证号")
    private String guestIdCard;

    /**
     * 入住人数
     */
    @Schema(description = "入住人数")
    private Integer guestCount;

    /**
     * 房间ID
     */
    @Schema(description = "房间ID")
    private Integer roomId;

    /**
     * 房间编号
     */
    @Schema(description = "房间编号")
    private String roomNumber;

    /**
     * 房型ID
     */
    @Schema(description = "房型ID")
    private Integer roomTypeId;

    /**
     * 房型名称
     */
    @Schema(description = "房型名称")
    private String roomTypeName;

    /**
     * 入住日期
     */
    @Schema(description = "入住日期")
    private String checkInDate;

    /**
     * 退房日期
     */
    @Schema(description = "退房日期")
    private String checkOutDate;

    /**
     * 入住天数
     */
    @Schema(description = "入住天数")
    private Integer nights;

    /**
     * 订单状态：pending-待入住，active-入住中，completed-已完成，canceled-已取消
     */
    @Schema(description = "订单状态")
    private String status;

    /**
     * 支付状态：unpaid-未支付，paid-已支付，refunded-已退款
     */
    @Schema(description = "支付状态")
    private String paymentStatus;
    
    /**
     * 总金额
     */
    @Schema(description = "总金额")
    private Integer totalAmount;
    
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private String createTime;
    
    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private String updateTime;
}

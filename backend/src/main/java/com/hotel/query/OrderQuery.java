package com.hotel.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 订单查询类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "订单查询类")
public class OrderQuery extends BaseQuery {

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
     * 房间ID
     */
    @Schema(description = "房间ID")
    private Integer roomId;

    /**
     * 房型ID
     */
    @Schema(description = "房型ID")
    private Integer roomTypeId;

    /**
     * 订单状态
     */
    @Schema(description = "订单状态")
    private String status;

    /**
     * 支付状态
     */
    @Schema(description = "支付状态")
    private String paymentStatus;

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
     * 房间编号
     */
    @Schema(description = "房间编号")
    private String roomNumber;
}

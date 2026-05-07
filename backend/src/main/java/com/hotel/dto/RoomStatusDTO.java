package com.hotel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 房间状态DTO
 */
@Data
@Schema(name = "房间状态DTO")
public class RoomStatusDTO {
    
    /**
     * 房间ID
     */
    @Schema(description = "房间ID")
    private Integer id;
    
    /**
     * 房间编号
     */
    @Schema(description = "房间编号")
    private String roomNumber;

    /**
     * 楼层
     */
    @Schema(description = "楼层")
    private Integer floor;

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
     * 可住人数
     */
    @Schema(description = "可住人数")
    private Integer capacity;
    
    /**
     * 房间状态
     */
    @Schema(description = "房间状态")
    private String status;
    
    /**
     * 清洁状态
     */
    @Schema(description = "清洁状态")
    private String cleanStatus;
    
    /**
     * 价格
     */
    @Schema(description = "价格")
    private Integer price;
}

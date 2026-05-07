package com.hotel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 房型DTO
 */
@Data
@Schema(name = "房型DTO")
public class RoomTypeDTO {
    
    /**
     * 房型ID
     */
    @Schema(description = "房型ID")
    private Integer id;
    
    /**
     * 房型名称
     */
    @Schema(description = "房型名称")
    private String name;
    
    /**
     * 可住人数
     */
    @Schema(description = "可住人数")
    private Integer capacity;
    
    /**
     * 床位描述
     */
    @Schema(description = "床位描述")
    private String beds;
    
    /**
     * 参考价格
     */
    @Schema(description = "参考价格")
    private String price;
    
    /**
     * 状态：active-启用，inactive-禁用
     */
    @Schema(description = "状态")
    private String status;
    
    /**
     * 房型封面图片URL
     */
    @Schema(description = "房型封面图片URL")
    private String image;
    
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

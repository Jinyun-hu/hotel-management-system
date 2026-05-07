package com.hotel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 房间DTO
 */
@Data
@Schema(name = "房间DTO")
public class RoomDTO {

    /**
     * 房间ID
     */
    @Schema(description = "房间ID")
    private Integer id;

    /**
     * 房间编号
     */
    @Schema(description = "房间编号")
    @NotBlank(message = "房间编号不能为空")
    @Size(max = 20, message = "房间编号长度不能超过20个字符")
    private String roomNumber;

    /**
     * 所属房型ID
     */
    @Schema(description = "所属房型ID")
    @NotNull(message = "房型ID不能为空")
    private Integer roomTypeId;

    /**
     * 所属房型名称
     */
    @Schema(description = "所属房型名称")
    private String roomTypeName;

    /**
     * 所在楼层
     */
    @Schema(description = "楼层")
    @NotNull(message = "楼层不能为空")
    @Min(value = 1, message = "楼层必须大于0")
    @Max(value = 100, message = "楼层不能超过100")
    private Integer floor;

    /**
     * 房间状态：available-空闲，occupied-已入住，cleaning-清洁中，maintenance-维修中，waiting_clean-等待清洁
     */
    @Schema(description = "房间状态")
    @NotBlank(message = "房间状态不能为空")
    @Pattern(regexp = "^(available|occupied|cleaning|maintenance|waiting_clean)$", message = "房间状态必须是：available、occupied、cleaning、maintenance或waiting_clean")
    private String status;

    /**
     * 清洁状态：clean-已清洁，dirty-脏，cleaning-清洁中
     */
    @Schema(description = "清洁状态")
    private String cleanStatus;

    /**
     * 勿扰模式：true-开启，false-关闭
     */
    @Schema(description = "勿扰模式")
    private Boolean doNotDisturb;

    /**
     * 价格
     */
    @Schema(description = "价格")
    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    private BigDecimal price;

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

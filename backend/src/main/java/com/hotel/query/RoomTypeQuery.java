package com.hotel.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 房型查询类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "房型查询类")
public class RoomTypeQuery extends BaseQuery {

    /**
     * 房型名称
     */
    @Schema(description = "房型名称")
    private String name;

    /**
     * 房型状态
     */
    @Schema(description = "房型状态")
    private String status;
}

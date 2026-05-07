package com.hotel.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 房间查询类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "房间查询类")
public class RoomQuery extends BaseQuery {

    /**
     * 房间编号
     */
    @Schema(description = "房间编号")
    private String roomNumber;

    /**
     * 所属房型ID
     */
    @Schema(description = "所属房型")
    private Integer roomTypeId;

    /**
     * 前端发送的房型参数
     */
    @Schema(description = "前端发送的房型参数")
    private Integer type;

    /**
     * 所属楼层
     */
    @Schema(description = "所属楼层")
    private Integer floor;

    /**
     * 房间状态
     */
    @Schema(description = "房间状态")
    private String status;
}

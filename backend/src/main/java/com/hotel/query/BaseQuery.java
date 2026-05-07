package com.hotel.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 基础查询类
 */
@Data
@Schema(name = "基础查询类")
public class BaseQuery {

    /**
     * 关键词（支持模糊查询）
     */
    @Schema(description = "关键词")
    private String keyword;

    /**
     * 状态
     */
    @Schema(description = "状态")
    private String status;

    /**
     * 当前页码
     */
    @Schema(description = "当前页码")
    private Integer page;

    /**
     * 每页条数
     */
    @Schema(description = "每页条数")
    private Integer size;
}

package com.hotel.controller;

import com.hotel.common.RestResult;
import com.hotel.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 数据统计控制器
 */
@Slf4j
@Tag(name = "数据统计", description = "数据统计相关接口")
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 获取运营统计数据
     *
     * @param dateRange 时间范围（天），默认7
     * @return 统计数据
     */
    @Operation(summary = "获取运营统计数据", description = "获取酒店运营统计数据")
    @GetMapping
    public RestResult<Map<String, Object>> getStatistics(
            @Parameter(description = "时间范围（天），默认7") @RequestParam(required = false, defaultValue = "7") Integer dateRange) {
        Map<String, Object> result = statisticsService.getStatistics(dateRange);
        return RestResult.success("success", result);
    }

    /**
     * 获取趋势数据
     *
     * @param dateRange 时间范围（天），默认7
     * @return 趋势数据
     */
    @Operation(summary = "获取趋势数据", description = "获取营收、订单等趋势数据")
    @GetMapping("/trend")
    public RestResult<Map<String, Object>> getTrendData(
            @Parameter(description = "时间范围（天），默认7") @RequestParam(required = false, defaultValue = "7") Integer dateRange) {
        Map<String, Object> result = statisticsService.getTrendData(dateRange);
        return RestResult.success("success", result);
    }

    /**
     * 获取房间状态分布
     *
     * @return 房间状态分布数据
     */
    @Operation(summary = "获取房间状态分布", description = "获取房间状态分布数据")
    @GetMapping("/room-status")
    public RestResult<Map<String, Object>> getRoomStatusDistribution() {
        Map<String, Object> result = statisticsService.getRoomStatusDistribution();
        return RestResult.success("success", result);
    }

    /**
     * 导出统计数据
     *
     * @param dateRange 时间范围（天），默认7
     * @param response  HTTP响应对象
     */
    @Operation(summary = "导出统计数据", description = "导出酒店运营统计数据为Excel文件")
    @GetMapping("/export")
    public void exportStatistics(
            @Parameter(description = "时间范围（天），默认7") @RequestParam(required = false, defaultValue = "7") Integer dateRange,
            HttpServletResponse response) {
        statisticsService.exportStatistics(dateRange, response);
    }
}

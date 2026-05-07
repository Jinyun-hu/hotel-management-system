package com.hotel.service;

import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 数据统计服务接口
 */
public interface StatisticsService {

    /**
     * 获取运营统计数据
     * @param dateRange 时间范围（天）
     * @return 统计数据
     */
    Map<String, Object> getStatistics(Integer dateRange);

    /**
     * 获取趋势数据
     * @param dateRange 时间范围（天）
     * @return 趋势数据，包含日期列表、营收趋势、订单趋势等
     */
    Map<String, Object> getTrendData(Integer dateRange);

    /**
     * 获取房间状态分布
     * @return 房间状态分布数据
     */
    Map<String, Object> getRoomStatusDistribution();

    /**
     * 导出统计数据为Excel
     * @param dateRange 时间范围（天）
     * @param response HTTP响应对象
     */
    void exportStatistics(Integer dateRange, HttpServletResponse response);
}

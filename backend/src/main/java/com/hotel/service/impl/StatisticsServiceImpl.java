package com.hotel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hotel.entity.OrdersDO;
import com.hotel.entity.RoomDO;
import com.hotel.mapper.OrdersMapper;
import com.hotel.mapper.RoomMapper;
import com.hotel.service.StatisticsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import java.util.stream.Collectors;

/**
 * 数据统计服务实现类
 */
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final OrdersMapper ordersMapper;
    private final RoomMapper roomMapper;

    /**
     * 获取运营统计数据
     */
    @Override
    public Map<String, Object> getStatistics(Integer dateRange) {
        Map<String, Object> result = new HashMap<>();

        // 今天的时间范围
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().plusDays(1).atStartOfDay();

        // 今日入住数
        LambdaQueryWrapper<OrdersDO> checkInWrapper = new LambdaQueryWrapper<>();
        checkInWrapper.ge(OrdersDO::getCheckInDate, todayStart.toLocalDate())
                     .lt(OrdersDO::getCheckInDate, todayEnd.toLocalDate());
        Long todayCheckInCount = ordersMapper.selectCount(checkInWrapper);

        // 今日实际入住且已支付的订单数
        LambdaQueryWrapper<OrdersDO> actualCheckInWrapper = new LambdaQueryWrapper<>();
        actualCheckInWrapper.ge(OrdersDO::getCheckInDate, todayStart.toLocalDate())
                          .lt(OrdersDO::getCheckInDate, todayEnd.toLocalDate())
                          .eq(OrdersDO::getStatus, "active")
                          .eq(OrdersDO::getPaymentStatus, "paid");
        Long actualCheckInCount = ordersMapper.selectCount(actualCheckInWrapper);

        // 总房间数
        Long totalRooms = roomMapper.selectCount(null);

        // 可用房间数
        LambdaQueryWrapper<RoomDO> availableWrapper = new LambdaQueryWrapper<>();
        availableWrapper.eq(RoomDO::getStatus, "available");
        Long availableRooms = roomMapper.selectCount(availableWrapper);

        // 楼层数
        List<RoomDO> rooms = roomMapper.selectList(null);
        Set<Integer> floors = new HashSet<>();
        for (RoomDO room : rooms) {
            if (room.getFloor() != null) {
                floors.add(room.getFloor());
            }
        }
        int floorCount = floors.size();

        // 今日订单数
        LambdaQueryWrapper<OrdersDO> todayOrderWrapper = new LambdaQueryWrapper<>();
        todayOrderWrapper.ge(OrdersDO::getCreateTime, todayStart)
                         .lt(OrdersDO::getCreateTime, todayEnd);
        Long todayOrderCount = ordersMapper.selectCount(todayOrderWrapper);

        // 今日营收（创建时间为今天且已支付的订单总金额）
        LambdaQueryWrapper<OrdersDO> revenueWrapper = new LambdaQueryWrapper<>();
        revenueWrapper.ge(OrdersDO::getCreateTime, todayStart)
                      .lt(OrdersDO::getCreateTime, todayEnd)
                      .eq(OrdersDO::getPaymentStatus, "paid");
        List<OrdersDO> paidOrders = ordersMapper.selectList(revenueWrapper);
        BigDecimal todayRevenue = paidOrders.stream()
                .map(OrdersDO::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 累计营收（所有已支付订单的总金额）
        LambdaQueryWrapper<OrdersDO> totalRevenueWrapper = new LambdaQueryWrapper<>();
        totalRevenueWrapper.eq(OrdersDO::getPaymentStatus, "paid");
        List<OrdersDO> allPaidOrders = ordersMapper.selectList(totalRevenueWrapper);
        BigDecimal totalRevenue = allPaidOrders.stream()
                .map(OrdersDO::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 入住率
        LambdaQueryWrapper<RoomDO> occupiedWrapper = new LambdaQueryWrapper<>();
        occupiedWrapper.eq(RoomDO::getStatus, "occupied");
        Long occupiedRooms = roomMapper.selectCount(occupiedWrapper);
        double occupancyRate = totalRooms > 0 ?
                (double) occupiedRooms / totalRooms * 100 : 0.0;

        // 构建结果
        result.put("todayCheckInCount", todayCheckInCount);
        result.put("actualCheckInCount", actualCheckInCount);
        result.put("totalRooms", totalRooms);
        result.put("availableRooms", availableRooms);
        result.put("floorCount", floorCount);
        result.put("todayOrderCount", todayOrderCount);
        result.put("todayRevenue", todayRevenue);
        result.put("totalRevenue", totalRevenue);
        result.put("occupancyRate", Math.round(occupancyRate * 100.0) / 100.0);

        return result;
    }

    /**
     * 获取趋势数据
     */
    @Override
    public Map<String, Object> getTrendData(Integer dateRange) {
        Map<String, Object> result = new HashMap<>();

        // 默认查询7天的数据
        if (dateRange == null) {
            dateRange = 7;
        }

        LocalDateTime startTime = LocalDate.now().minusDays(dateRange).atStartOfDay();
        LocalDateTime endTime = LocalDate.now().plusDays(1).atStartOfDay();

        // 查询订单数据
        LambdaQueryWrapper<OrdersDO> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.ge(OrdersDO::getCreateTime, startTime)
                   .lt(OrdersDO::getCreateTime, endTime)
                   .orderByAsc(OrdersDO::getCreateTime);
        List<OrdersDO> orders = ordersMapper.selectList(orderWrapper);

        // 按日期分组
        Map<LocalDate, List<OrdersDO>> groupedByDate = orders.stream()
                .collect(Collectors.groupingBy(order -> order.getCreateTime().toLocalDate()));

        // 生成日期列表和对应数据
        List<String> dates = new ArrayList<>();
        List<BigDecimal> revenues = new ArrayList<>();
        List<Integer> orderCounts = new ArrayList<>();
        List<Integer> checkInCounts = new ArrayList<>();

        for (int i = dateRange - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            dates.add(date.toString());

            List<OrdersDO> dayOrders = groupedByDate.getOrDefault(date, Collections.emptyList());

            // 营收
            BigDecimal dayRevenue = dayOrders.stream()
                    .filter(order -> "paid".equals(order.getPaymentStatus()))
                    .map(OrdersDO::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            revenues.add(dayRevenue);

            // 订单数
            orderCounts.add(dayOrders.size());

            // 入住数
            long checkInCount = dayOrders.stream()
                    .filter(order -> date.equals(order.getCheckInDate()))
                    .count();
            checkInCounts.add((int) checkInCount);
        }

        result.put("dates", dates);
        result.put("revenues", revenues);
        result.put("orderCounts", orderCounts);
        result.put("checkInCounts", checkInCounts);

        return result;
    }

    /**
     * 获取房间状态分布
     */
    @Override
    public Map<String, Object> getRoomStatusDistribution() {
        Map<String, Object> result = new HashMap<>();

        List<RoomDO> rooms = roomMapper.selectList(null);

        // 统计各状态房间数
        Map<String, Long> statusCounts = rooms.stream()
                .collect(Collectors.groupingBy(RoomDO::getStatus, Collectors.counting()));

        result.put("occupied", statusCounts.getOrDefault("occupied", 0L));
        result.put("available", statusCounts.getOrDefault("available", 0L));
        result.put("cleaning", statusCounts.getOrDefault("cleaning", 0L));
        result.put("maintenance", statusCounts.getOrDefault("maintenance", 0L));

        return result;
    }

    /**
     * 导出统计数据为Excel
     */
    @Override
    public void exportStatistics(Integer dateRange, HttpServletResponse response) {
        try {
            // 默认查询7天的数据
            if (dateRange == null) {
                dateRange = 7;
            }

            LocalDateTime startTime = LocalDate.now().minusDays(dateRange).atStartOfDay();
            LocalDateTime endTime = LocalDate.now().plusDays(1).atStartOfDay();

            // 查询订单数据
            LambdaQueryWrapper<OrdersDO> orderWrapper = new LambdaQueryWrapper<>();
            orderWrapper.ge(OrdersDO::getCreateTime, startTime)
                       .lt(OrdersDO::getCreateTime, endTime)
                       .orderByAsc(OrdersDO::getCreateTime);
            List<OrdersDO> orders = ordersMapper.selectList(orderWrapper);

            // 创建工作簿
            Workbook workbook = new XSSFWorkbook();

            // 创建样式
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            CellStyle currencyStyle = createCurrencyStyle(workbook);

            // 第一个Sheet：每日统计汇总
            Sheet summarySheet = workbook.createSheet("每日统计");
            createSummarySheet(summarySheet, orders, headerStyle, dateStyle, currencyStyle);

            // 第二个Sheet：订单明细
            Sheet detailSheet = workbook.createSheet("订单明细");
            createDetailSheet(detailSheet, orders, headerStyle, dateStyle, currencyStyle);

            // 设置响应头
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String fileName = "统计数据_" + sdf.format(new Date()) + ".xlsx";
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition",
                    "attachment; filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));

            // 写入输出流
            OutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();

        } catch (Exception e) {
            throw new RuntimeException("导出Excel失败: " + e.getMessage(), e);
        }
    }

    /**
     * 创建表头样式
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    /**
     * 创建日期样式
     */
    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("yyyy-mm-dd"));
        return style;
    }

    /**
     * 创建货币样式
     */
    private CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0.00"));
        return style;
    }

    /**
     * 创建每日统计汇总Sheet
     */
    private void createSummarySheet(Sheet sheet, List<OrdersDO> orders, CellStyle headerStyle,
                                    CellStyle dateStyle, CellStyle currencyStyle) {
        // 按日期分组统计
        Map<LocalDate, List<OrdersDO>> groupedByDate = orders.stream()
                .collect(Collectors.groupingBy(order -> order.getCreateTime().toLocalDate()));

        // 表头
        Row headerRow = sheet.createRow(0);
        String[] headers = {"日期", "订单数", "营收金额", "平均订单金额"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 4000);
        }

        // 数据行
        int rowNum = 1;
        for (Map.Entry<LocalDate, List<OrdersDO>> entry : groupedByDate.entrySet().stream()
                .sorted(Map.Entry.comparingByKey()).collect(Collectors.toList())) {
            Row row = sheet.createRow(rowNum++);

            List<OrdersDO> dayOrders = entry.getValue();
            BigDecimal dayRevenue = dayOrders.stream()
                    .map(OrdersDO::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal avgOrderAmount = dayOrders.isEmpty() ? BigDecimal.ZERO :
                    dayRevenue.divide(new BigDecimal(dayOrders.size()), 2, java.math.RoundingMode.HALF_UP);

            // 日期
            Cell dateCell = row.createCell(0);
            dateCell.setCellValue(entry.getKey().toString());
            dateCell.setCellStyle(dateStyle);

            // 订单数
            Cell countCell = row.createCell(1);
            countCell.setCellValue(dayOrders.size());

            // 营收金额
            Cell revenueCell = row.createCell(2);
            revenueCell.setCellValue(dayRevenue.doubleValue());
            revenueCell.setCellStyle(currencyStyle);

            // 平均订单金额
            Cell avgCell = row.createCell(3);
            avgCell.setCellValue(avgOrderAmount.doubleValue());
            avgCell.setCellStyle(currencyStyle);
        }
    }

    /**
     * 创建订单明细Sheet
     */
    private void createDetailSheet(Sheet sheet, List<OrdersDO> orders, CellStyle headerStyle,
                                    CellStyle dateStyle, CellStyle currencyStyle) {
        // 表头
        Row headerRow = sheet.createRow(0);
        String[] headers = {"订单ID", "订单号", "客人姓名", "客人电话", "房间ID", "房型ID", "入住日期", "退房日期",
                "住宿天数", "订单状态", "支付状态", "总金额", "创建时间"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 3000);
        }
        sheet.setColumnWidth(11, 5000); // 总金额
        sheet.setColumnWidth(12, 5000); // 创建时间

        // 数据行
        int rowNum = 1;
        for (OrdersDO order : orders) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(order.getId());
            row.createCell(1).setCellValue(order.getOrderNo() != null ? order.getOrderNo() : "");
            row.createCell(2).setCellValue(order.getGuestName() != null ? order.getGuestName() : "");
            row.createCell(3).setCellValue(order.getGuestPhone() != null ? order.getGuestPhone() : "");
            row.createCell(4).setCellValue(order.getRoomId() != null ? order.getRoomId().toString() : "");
            row.createCell(5).setCellValue(order.getRoomTypeId() != null ? order.getRoomTypeId().toString() : "");
            row.createCell(6).setCellValue(order.getCheckInDate().toString());
            row.createCell(7).setCellValue(order.getCheckOutDate().toString());
            row.createCell(8).setCellValue(order.getNights() != null ? order.getNights() : 0);
            row.createCell(9).setCellValue(order.getStatus() != null ? order.getStatus() : "");
            row.createCell(10).setCellValue(order.getPaymentStatus() != null ? order.getPaymentStatus() : "");

            Cell amountCell = row.createCell(11);
            amountCell.setCellValue(order.getTotalAmount() != null ? order.getTotalAmount().doubleValue() : 0.0);
            amountCell.setCellStyle(currencyStyle);

            row.createCell(12).setCellValue(order.getCreateTime().toString());
        }
    }
}

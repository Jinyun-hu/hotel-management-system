package com.hotel.controller;

import com.hotel.common.RestResult;
import com.hotel.service.RoomStatusService;
import com.hotel.task.RoomStatusCheckTask;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 房态可视化控制器
 */
@Slf4j
@Tag(name = "房态可视化", description = "房态可视化相关接口")
@RestController
@RequestMapping("/api/room-status")
public class RoomStatusController {

    @Autowired
    private RoomStatusService roomStatusService;
    
    @Autowired
    private RoomStatusCheckTask roomStatusCheckTask;

    /**
     * 获取房态数据
     *
     * @param floor 楼层筛选
     * @return 房态数据
     */
    @Operation(summary = "获取房态数据", description = "获取所有房间的实时状态，用于可视化展示")
    @GetMapping
    public RestResult<Map<String, Object>> getRoomStatus(
            @Parameter(description = "楼层筛选") @RequestParam(required = false) Integer floor) {
        Map<String, Object> result = roomStatusService.getRoomStatus(floor);
        return RestResult.success("success", result);
    }

    /**
     * 更新房间状态
     *
     * @param id           房间ID
     * @param requestBody  状态信息
     * @return 更新结果
     */
    @Operation(summary = "更新房间状态", description = "快速更新房间状态（入住、退房、清洁、维修等）")
    @PutMapping("/{id}")
    public RestResult<Void> updateRoomStatus(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> requestBody) {
        String status = (String) requestBody.get("status");
        String cleanStatus = (String) requestBody.get("cleanStatus");
        Boolean doNotDisturb = (Boolean) requestBody.get("doNotDisturb");
        roomStatusService.updateRoomStatus(id, status, cleanStatus, doNotDisturb);
        return RestResult.success();
    }
    
    /**
     * 手动触发房间状态检查
     *
     * @return 检查结果
     */
    @Operation(summary = "手动触发房间状态检查", description = "手动执行房间状态与订单状态一致性检查")
    @GetMapping("/check")
    public RestResult<String> checkRoomStatus() {
        roomStatusCheckTask.checkRoomStatusConsistency();
        return RestResult.success("房间状态检查已执行");
    }
}

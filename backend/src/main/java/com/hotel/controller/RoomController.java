package com.hotel.controller;

import com.hotel.common.PageResult;
import com.hotel.common.RestResult;
import com.hotel.dto.RoomDTO;
import com.hotel.entity.OrdersDO;
import com.hotel.query.RoomQuery;
import com.hotel.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 房间管理控制器
 */
@Slf4j
@Tag(name = "房间管理", description = "房间管理相关接口")
@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;
    @Operation(summary = "查询房间列表", description = "获取所有房间列表，支持搜索和筛选")
    @GetMapping
    public RestResult<PageResult<RoomDTO>> listRooms(RoomQuery query) {
        PageResult<RoomDTO> result = roomService.listRooms(query);
        return RestResult.success("success", result);
    }

    @Operation(summary = "新增房间", description = "添加新的房间")
    @PostMapping
    public RestResult<RoomDTO> addRoom(@Valid @RequestBody RoomDTO roomDTO) {
        RoomDTO result = roomService.addRoom(roomDTO);
        return RestResult.success("房间添加成功", result);
    }


    @Operation(summary = "编辑房间", description = "修改房间信息")
    @PutMapping("/{id}")
    public RestResult<Void> updateRoom(@PathVariable Integer id, @Valid @RequestBody RoomDTO roomDTO) {
        roomService.updateRoom(id, roomDTO);
        return RestResult.success();
    }

    @Operation(summary = "删除房间", description = "删除指定房间")
    @DeleteMapping("/{id}")
    public RestResult<Void> deleteRoom(@PathVariable Integer id) {
        roomService.deleteRoom(id);
        return RestResult.success();
    }

    @Operation(summary = "检查房间关联的订单", description = "检查指定房间关联的订单")
    @GetMapping("/{id}/orders")
    public RestResult<List<OrdersDO>> checkRoomOrders(@PathVariable Integer id) {
        List<OrdersDO> orders = roomService.checkRoomOrders(id);
        return RestResult.success("success", orders);
    }
}

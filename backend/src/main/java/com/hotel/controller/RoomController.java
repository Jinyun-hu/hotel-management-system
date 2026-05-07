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

    /**
     * 查询房间列表
     *
     * @param query 查询条件
     * @return 房间列表
     */
    @Operation(summary = "查询房间列表", description = "获取所有房间列表，支持搜索和筛选")
    @GetMapping
    public RestResult<PageResult<RoomDTO>> listRooms(RoomQuery query) {
        PageResult<RoomDTO> result = roomService.listRooms(query);
        return RestResult.success("success", result);
    }

    /**
     * 新增房间
     *
     * @param roomDTO 房间信息
     * @return 新增的房间
     */
    @Operation(summary = "新增房间", description = "添加新的房间")
    @PostMapping
    public RestResult<RoomDTO> addRoom(@Valid @RequestBody RoomDTO roomDTO) {
        RoomDTO result = roomService.addRoom(roomDTO);
        return RestResult.success("房间添加成功", result);
    }

    /**
     * 编辑房间
     *
     * @param id       房间ID
     * @param roomDTO  房间信息
     * @return 更新结果
     */
    @Operation(summary = "编辑房间", description = "修改房间信息")
    @PutMapping("/{id}")
    public RestResult<Void> updateRoom(@PathVariable Integer id, @Valid @RequestBody RoomDTO roomDTO) {
        roomService.updateRoom(id, roomDTO);
        return RestResult.success();
    }

    /**
     * 删除房间
     *
     * @param id 房间ID
     * @return 删除结果
     */
    @Operation(summary = "删除房间", description = "删除指定房间")
    @DeleteMapping("/{id}")
    public RestResult<Void> deleteRoom(@PathVariable Integer id) {
        roomService.deleteRoom(id);
        return RestResult.success();
    }

    /**
     * 检查房间关联的订单
     *
     * @param id 房间ID
     * @return 关联的订单列表
     */
    @Operation(summary = "检查房间关联的订单", description = "检查指定房间关联的订单")
    @GetMapping("/{id}/orders")
    public RestResult<List<OrdersDO>> checkRoomOrders(@PathVariable Integer id) {
        List<OrdersDO> orders = roomService.checkRoomOrders(id);
        return RestResult.success("success", orders);
    }
}

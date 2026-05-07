package com.hotel.controller;

import com.hotel.common.PageResult;
import com.hotel.common.RestResult;
import com.hotel.dto.RoomTypeDTO;
import com.hotel.entity.OrdersDO;
import com.hotel.query.RoomTypeQuery;
import com.hotel.service.RoomTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;



/**
 * 房型管理控制器
 */
@Slf4j
@Tag(name = "房型管理", description = "房型管理相关接口")
@RestController
@RequestMapping("/api/room-types")
public class RoomTypeController {

    @Autowired
    private RoomTypeService roomTypeService;

    /**
     * 查询房型列表
     *
     * @param query 查询条件
     * @return 房型列表
     */
    @Operation(summary = "查询房型列表", description = "获取所有房型列表，支持搜索和筛选")
    @GetMapping
    public RestResult<PageResult<RoomTypeDTO>> listRoomTypes(RoomTypeQuery query) {
        PageResult<RoomTypeDTO> result = roomTypeService.listRoomTypes(query);
        return RestResult.success("success", result);
    }

    /**
     * 新增房型
     *
     * @param roomTypeDTO 房型信息
     * @return 新增的房型
     */
    @Operation(summary = "新增房型", description = "添加新的房型")
    @PostMapping
    public RestResult<RoomTypeDTO> addRoomType(@RequestBody RoomTypeDTO roomTypeDTO) {
        RoomTypeDTO result = roomTypeService.addRoomType(roomTypeDTO);
        return RestResult.success("房型添加成功", result);
    }

    /**
     * 编辑房型
     *
     * @param id           房型ID
     * @param roomTypeDTO  房型信息
     * @return 更新结果
     */
    @Operation(summary = "编辑房型", description = "修改房型信息")
    @PutMapping("/{id}")
    public RestResult<Void> updateRoomType(@PathVariable Integer id, @RequestBody RoomTypeDTO roomTypeDTO) {
        roomTypeService.updateRoomType(id, roomTypeDTO);
        return RestResult.success();
    }

    /**
     * 删除房型
     *
     * @param id 房型ID
     * @return 删除结果
     */
    @Operation(summary = "删除房型", description = "删除指定房型")
    @DeleteMapping("/{id}")
    public RestResult<Void> deleteRoomType(@PathVariable Integer id) {
        roomTypeService.deleteRoomType(id);
        return RestResult.success();
    }

    /**
     * 检查房型关联的订单
     *
     * @param id 房型ID
     * @return 关联的订单列表
     */
    @Operation(summary = "检查房型关联的订单", description = "检查指定房型关联的订单")
    @GetMapping("/{id}/orders")
    public RestResult<List<OrdersDO>> checkRoomTypeOrders(@PathVariable Integer id) {
        List<OrdersDO> orders = roomTypeService.checkRoomTypeOrders(id);
        return RestResult.success("success", orders);
    }

    /**
     * 更新房型状态
     *
     * @param id 房型ID
     * @param status 状态信息
     * @return 更新结果
     */
    @Operation(summary = "更新房型状态", description = "启用或停用房型")
    @PatchMapping("/{id}/status")
    public RestResult<Void> updateRoomTypeStatus(@PathVariable Integer id, @RequestBody StatusRequest status) {
        roomTypeService.updateRoomTypeStatus(id, status.getStatus());
        return RestResult.success();
    }

    /**
     * 状态请求对象
     */
    static class StatusRequest {
        private String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}

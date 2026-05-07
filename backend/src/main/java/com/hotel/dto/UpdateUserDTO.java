package com.hotel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新用户信息DTO
 */
@Data
@Schema(name = "更新用户信息DTO")
public class UpdateUserDTO {

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    @Size(max = 50, message = "用户名长度不能超过50个字符")
    private String username;

    /**
     * 姓名
     */
    @Schema(description = "姓名")
    @Size(max = 50, message = "姓名长度不能超过50个字符")
    private String name;

    /**
     * 角色
     */
    @Schema(description = "角色")
    private String role;
    
    /**
     * 头像
     */
    @Schema(description = "头像")
    private String avatar;
}

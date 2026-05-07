package com.hotel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 用户注册DTO
 */
@Data
@Schema(name = "用户注册DTO")
public class RegisterDTO {

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;

    /**
     * 密码
     */
    @Schema(description = "密码")
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String password;

    /**
     * 确认密码（可选，用于前端验证）
     */
    @Schema(description = "确认密码")
    private String confirmPassword;

    /**
     * 姓名
     */
    @Schema(description = "姓名")
    @NotBlank(message = "姓名不能为空")
    @Size(max = 50, message = "姓名长度不能超过50个字符")
    private String name;

    /**
     * 角色（可选，默认为user）
     */
    @Schema(description = "角色：admin-管理员，user-普通用户（默认）")
    private String role;
}

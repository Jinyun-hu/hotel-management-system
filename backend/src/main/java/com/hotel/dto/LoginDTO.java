package com.hotel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户登录DTO
 */
@Data
@Schema(name = "用户登录DTO")
public class LoginDTO {
    
    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;
    
    /**
     * 密码
     */
    @Schema(description = "密码")
    private String password;
    
    /**
     * 是否记住登录状态
     */
    @Schema(description = "是否记住登录状态")
    private Boolean rememberMe;
}

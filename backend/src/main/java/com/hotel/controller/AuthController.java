package com.hotel.controller;

import com.hotel.common.RestResult;
import com.hotel.dto.LoginDTO;
import com.hotel.dto.RegisterDTO;
import com.hotel.dto.UpdateUserDTO;
import com.hotel.entity.SysUserDO;
import com.hotel.service.SysUserService;
import com.hotel.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户认证控制器
 */
@Slf4j
@Tag(name = "用户认证", description = "用户认证相关接口")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户登录
     *
     * @param loginDTO 登录信息
     * @return 登录结果
     */
    @Operation(summary = "用户登录", description = "用户登录验证接口")
    @PostMapping("/login")
    public RestResult<Map<String, Object>> login(@RequestBody LoginDTO loginDTO) {
        SysUserDO user = sysUserService.login(loginDTO);

        // 生成token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user);

        return RestResult.success("登录成功", data);
    }

    /**
     * 用户注册
     *
     * @param registerDTO 注册信息
     * @return 注册结果
     */
    @Operation(summary = "用户注册", description = "新用户注册接口")
    @PostMapping("/register")
    public RestResult<Map<String, Object>> register(@Valid @RequestBody RegisterDTO registerDTO) {
        SysUserDO user = sysUserService.register(registerDTO);

        // 注册成功后自动登录
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user);

        return RestResult.success("注册成功", data);
    }

    /**
     * 用户退出
     *
     * @return 退出结果
     */
    @Operation(summary = "用户退出", description = "用户退出登录")
    @PostMapping("/logout")
    public RestResult<Void> logout() {
        sysUserService.logout();
        return RestResult.success();
    }

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户信息")
    @GetMapping("/current")
    public RestResult<SysUserDO> getCurrentUser() {
        SysUserDO user = sysUserService.getCurrentUser();
        return RestResult.success("success", user);
    }

    /**
     * 更新用户资料
     *
     * @param updateUserDTO 用户资料信息
     * @return 更新结果
     */
    @Operation(summary = "更新用户资料", description = "更新当前用户资料")
    @PutMapping("/update-profile")
    public RestResult<SysUserDO> updateProfile(@Valid @RequestBody UpdateUserDTO updateUserDTO) {
        SysUserDO user = sysUserService.updateProfile(updateUserDTO);
        return RestResult.success("用户资料更新成功", user);
    }

    /**
     * 获取所有用户列表
     *
     * @return 用户列表
     */
    @Operation(summary = "获取所有用户列表", description = "获取所有用户列表（仅超级管理员和管理员可用）")
    @GetMapping("/users")
    public RestResult<List<SysUserDO>> getUserList() {
        List<SysUserDO> userList = sysUserService.getUserList();
        return RestResult.success("success", userList);
    }
    
    /**
     * 更新用户信息
     *
     * @param id 用户ID
     * @param updateUserDTO 用户资料信息
     * @return 更新结果
     */
    @Operation(summary = "更新用户信息", description = "更新指定用户的信息，仅超级管理员和管理员可用")
    @PutMapping("/users/{id}")
    public RestResult<SysUserDO> updateUser(@PathVariable Integer id, @Valid @RequestBody UpdateUserDTO updateUserDTO) {
        SysUserDO user = sysUserService.updateUser(id, updateUserDTO);
        return RestResult.success("用户信息更新成功", user);
    }
    
    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @return 删除结果
     */
    @Operation(summary = "删除用户", description = "删除用户（仅超级管理员可用）")
    @DeleteMapping("/users/{userId}")
    public RestResult<String> deleteUser(@Parameter(description = "用户ID") @PathVariable Integer userId) {
        sysUserService.deleteUser(userId);
        return RestResult.success("删除成功");
    }
}

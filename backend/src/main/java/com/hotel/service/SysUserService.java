package com.hotel.service;

import com.hotel.dto.LoginDTO;
import com.hotel.dto.RegisterDTO;
import com.hotel.dto.UpdateUserDTO;
import com.hotel.entity.SysUserDO;

/**
 * 用户服务接口
 */
public interface SysUserService {

    /**
     * 用户登录
     * @param loginDTO 登录信息
     * @return 用户信息
     */
    SysUserDO login(LoginDTO loginDTO);

    /**
     * 用户注册
     * @param registerDTO 注册信息
     * @return 用户信息
     */
    SysUserDO register(RegisterDTO registerDTO);

    /**
     * 用户退出
     */
    void logout();

    /**
     * 获取当前用户信息
     * @return 用户信息
     */
    SysUserDO getCurrentUser();

    /**
     * 更新用户资料
     * @param updateUserDTO 用户资料信息
     * @return 更新后的用户信息
     */
    SysUserDO updateProfile(UpdateUserDTO updateUserDTO);

    /**
     * 更新其他用户信息（仅超级管理员和管理员可用）
     * @param userId 用户ID
     * @param updateUserDTO 用户资料信息
     * @return 更新后的用户信息
     */
    SysUserDO updateUser(Integer userId, UpdateUserDTO updateUserDTO);

    /**
     * 获取所有用户列表
     * @return 用户列表
     */
    java.util.List<SysUserDO> getUserList();
    
    /**
     * 检查并修复超级管理员账号
     */
    void checkAndFixSuperAdmin();
    
    /**
     * 删除用户
     * @param userId 用户ID
     */
    void deleteUser(Integer userId);
}

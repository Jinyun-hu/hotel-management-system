package com.hotel.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hotel.common.BusinessException;
import com.hotel.common.ResultCodeConstant;
import com.hotel.dto.LoginDTO;
import com.hotel.dto.RegisterDTO;
import com.hotel.dto.UpdateUserDTO;
import com.hotel.entity.SysUserDO;
import com.hotel.mapper.SysUserMapper;
import com.hotel.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {

    private final SysUserMapper sysUserMapper;

    /**
     * 用户登录
     */
    @Override
    public SysUserDO login(LoginDTO loginDTO) {
        LambdaQueryWrapper<SysUserDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserDO::getUsername, loginDTO.getUsername());
        SysUserDO user = sysUserMapper.selectOne(wrapper);

        if (user == null) {
            throw new BusinessException(ResultCodeConstant.USER_NOT_FOUND);
        }

        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCodeConstant.USER_DISABLED);
        }

        if (!BCrypt.checkpw(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCodeConstant.USERNAME_OR_PASSWORD_ERROR);
        }

        return user;
    }

    /**
     * 用户注册
     */
    @Override
    public SysUserDO register(RegisterDTO registerDTO) {
        // 验证两次密码是否一致
        String password = registerDTO.getPassword();
        String confirmPassword = registerDTO.getConfirmPassword();

        // 兼容前端没有传 confirmPassword 的情况
        if (confirmPassword != null && !confirmPassword.isEmpty()) {
            if (!password.equals(confirmPassword)) {
                throw new BusinessException("两次输入的密码不一致");
            }
        }

        // 检查用户名是否已存在
        LambdaQueryWrapper<SysUserDO> usernameWrapper = new LambdaQueryWrapper<>();
        usernameWrapper.eq(SysUserDO::getUsername, registerDTO.getUsername());
        if (sysUserMapper.selectOne(usernameWrapper) != null) {
            throw new BusinessException(ResultCodeConstant.USER_ALREADY_EXISTS);
        }

        // 创建新用户
        SysUserDO user = new SysUserDO();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        user.setName(registerDTO.getName());
        user.setStatus(1); // 默认启用

        // 设置角色，默认为admin
        String role = registerDTO.getRole();
        if (role == null || role.trim().isEmpty()) {
            role = "admin";
        }
        user.setRole(role);

        // 保存用户
        sysUserMapper.insert(user);

        // 返回用户信息（不包含密码）
        user.setPassword(null);
        return user;
    }

    /**
     * 用户退出
     */
    @Override
    public void logout() {
        // JWT无状态，无需实现
    }

    /**
     * 获取当前用户信息
     */
    @Override
    public SysUserDO getCurrentUser() {
        Integer userId = com.hotel.interceptor.JwtInterceptor.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }

        SysUserDO user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCodeConstant.USER_NOT_FOUND);
        }

        // 不返回密码
        user.setPassword(null);
        return user;
    }

    /**
     * 更新用户资料
     */
    @Override
    public SysUserDO updateProfile(UpdateUserDTO updateUserDTO) {
        Integer userId = com.hotel.interceptor.JwtInterceptor.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }

        SysUserDO user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCodeConstant.USER_NOT_FOUND);
        }

        // 更新用户名
        if (updateUserDTO.getUsername() != null && !updateUserDTO.getUsername().trim().isEmpty()) {
            // 检查用户名是否已被其他用户使用
            LambdaQueryWrapper<SysUserDO> usernameWrapper = new LambdaQueryWrapper<>();
            usernameWrapper.eq(SysUserDO::getUsername, updateUserDTO.getUsername());
            usernameWrapper.ne(SysUserDO::getId, userId); // 排除当前用户
            if (sysUserMapper.selectOne(usernameWrapper) != null) {
                throw new BusinessException(ResultCodeConstant.USER_ALREADY_EXISTS);
            }
            user.setUsername(updateUserDTO.getUsername());
        }

        // 更新姓名
        if (updateUserDTO.getName() != null && !updateUserDTO.getName().trim().isEmpty()) {
            user.setName(updateUserDTO.getName());
        }

        // 更新头像
        if (updateUserDTO.getAvatar() != null) {
            user.setAvatar(updateUserDTO.getAvatar());
        }

        // 更新时间会自动填充
        sysUserMapper.updateById(user);

        // 不返回密码
        user.setPassword(null);
        return user;
    }

    /**
     * 更新其他用户信息（仅超级管理员和管理员可用）
     */
    @Override
    public SysUserDO updateUser(Integer userId, UpdateUserDTO updateUserDTO) {
        Integer currentUserId = com.hotel.interceptor.JwtInterceptor.getCurrentUserId();
        if (currentUserId == null) {
            throw new BusinessException("用户未登录");
        }

        // 获取当前用户信息
        SysUserDO currentUser = sysUserMapper.selectById(currentUserId);
        if (currentUser == null) {
            throw new BusinessException(ResultCodeConstant.USER_NOT_FOUND);
        }

        // 检查权限：只有超级管理员可以管理用户
        if (!"super_admin".equals(currentUser.getRole())) {
            throw new BusinessException("权限不足，无法管理用户");
        }

        // 获取要更新的用户信息
        SysUserDO user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCodeConstant.USER_NOT_FOUND);
        }

        // 权限控制：只有超级管理员可以修改其他管理员的角色
        if ("admin".equals(user.getRole()) && !"super_admin".equals(currentUser.getRole())) {
            throw new BusinessException("权限不足，无法修改管理员角色");
        }

        // 权限控制：只有超级管理员可以设置超级管理员角色
        if ("super_admin".equals(updateUserDTO.getRole()) && !"super_admin".equals(currentUser.getRole())) {
            throw new BusinessException("权限不足，无法设置超级管理员角色");
        }

        // 更新用户名
        if (updateUserDTO.getUsername() != null && !updateUserDTO.getUsername().trim().isEmpty()) {
            // 检查用户名是否已被其他用户使用
            LambdaQueryWrapper<SysUserDO> usernameWrapper = new LambdaQueryWrapper<>();
            usernameWrapper.eq(SysUserDO::getUsername, updateUserDTO.getUsername());
            usernameWrapper.ne(SysUserDO::getId, userId); // 排除当前用户
            if (sysUserMapper.selectOne(usernameWrapper) != null) {
                throw new BusinessException(ResultCodeConstant.USER_ALREADY_EXISTS);
            }
            user.setUsername(updateUserDTO.getUsername());
        }

        // 更新姓名
        if (updateUserDTO.getName() != null && !updateUserDTO.getName().trim().isEmpty()) {
            user.setName(updateUserDTO.getName());
        }

        // 更新角色
        if (updateUserDTO.getRole() != null && !updateUserDTO.getRole().trim().isEmpty()) {
            user.setRole(updateUserDTO.getRole());
        }

        // 更新头像
        if (updateUserDTO.getAvatar() != null) {
            user.setAvatar(updateUserDTO.getAvatar());
        }

        // 更新时间会自动填充
        sysUserMapper.updateById(user);

        // 不返回密码
        user.setPassword(null);
        return user;
    }

    /**
     * 获取所有用户列表
     */
    @Override
    public java.util.List<SysUserDO> getUserList() {
        Integer currentUserId = com.hotel.interceptor.JwtInterceptor.getCurrentUserId();
        if (currentUserId == null) {
            throw new BusinessException("用户未登录");
        }

        // 获取当前用户信息
        SysUserDO currentUser = sysUserMapper.selectById(currentUserId);
        if (currentUser == null) {
            throw new BusinessException(ResultCodeConstant.USER_NOT_FOUND);
        }

        // 检查权限：只有超级管理员可以查看用户列表
        if (!"super_admin".equals(currentUser.getRole())) {
            throw new BusinessException("权限不足，无法查看用户列表");
        }

        // 查询所有用户
        java.util.List<SysUserDO> userList = sysUserMapper.selectList(null);

        // 不返回密码
        for (SysUserDO user : userList) {
            user.setPassword(null);
        }

        return userList;
    }
    
    /**
     * 检查并修复超级管理员账号
     */
    public void checkAndFixSuperAdmin() {
        LambdaQueryWrapper<SysUserDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserDO::getUsername, "superadmin");
        SysUserDO superAdmin = sysUserMapper.selectOne(wrapper);
        
        if (superAdmin == null) {
            // 创建超级管理员账号
            superAdmin = new SysUserDO();
            superAdmin.setUsername("superadmin");
            superAdmin.setPassword(BCrypt.hashpw("123456", BCrypt.gensalt()));
            superAdmin.setName("超级管理员");
            superAdmin.setRole("super_admin");
            superAdmin.setStatus(1);
            sysUserMapper.insert(superAdmin);
            System.out.println("超级管理员账号已创建");
        } else {
            // 检查密码是否正确
            if (!BCrypt.checkpw("123456", superAdmin.getPassword())) {
                // 修复密码
                superAdmin.setPassword(BCrypt.hashpw("123456", BCrypt.gensalt()));
                sysUserMapper.updateById(superAdmin);
                System.out.println("超级管理员密码已修复");
            }
        }
    }
    
    /**
     * 删除用户
     */
    @Override
    public void deleteUser(Integer userId) {
        Integer currentUserId = com.hotel.interceptor.JwtInterceptor.getCurrentUserId();
        if (currentUserId == null) {
            throw new BusinessException("用户未登录");
        }
        
        // 获取当前用户信息
        SysUserDO currentUser = sysUserMapper.selectById(currentUserId);
        if (currentUser == null) {
            throw new BusinessException(ResultCodeConstant.USER_NOT_FOUND);
        }
        
        // 检查权限：只有超级管理员可以删除用户
        if (!"super_admin".equals(currentUser.getRole())) {
            throw new BusinessException("权限不足，无法删除用户");
        }
        
        // 获取要删除的用户信息
        SysUserDO user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCodeConstant.USER_NOT_FOUND);
        }
        
        // 禁止删除超级管理员
        if ("super_admin".equals(user.getRole())) {
            throw new BusinessException("无法删除超级管理员账号");
        }
        
        // 禁止删除自己
        if (userId.equals(currentUserId)) {
            throw new BusinessException("无法删除当前登录账号");
        }
        
        // 删除用户
        sysUserMapper.deleteById(userId);
    }
}

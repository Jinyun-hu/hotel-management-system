package com.hotel.config;

import com.hotel.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 应用启动时执行的任务
 */
@Component
@RequiredArgsConstructor
public class ApplicationStartupRunner implements CommandLineRunner {

    private final SysUserService sysUserService;

    @Override
    public void run(String... args) throws Exception {
        // 检查并修复超级管理员账号
        sysUserService.checkAndFixSuperAdmin();
    }
}

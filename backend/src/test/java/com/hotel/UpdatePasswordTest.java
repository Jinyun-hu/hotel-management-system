package com.hotel;

import cn.hutool.crypto.digest.BCrypt;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class UpdatePasswordTest {

    @Test
    public void updatePassword() {
        try {
            // 加载驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // 连接数据库
            String url = "jdbc:mysql://localhost:3306/hotel_management?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false";
            String username = "root";
            String password = "20051012@Hjy";
            Connection conn = DriverManager.getConnection(url, username, password);
            
            // 生成密码哈希
            String newPassword = "123456";
            String hash = BCrypt.hashpw(newPassword);
            System.out.println("生成的密码哈希: " + hash);
            
            // 更新密码
            String sql = "UPDATE sys_user SET password = ? WHERE username = 'admin'";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, hash);
            int rows = pstmt.executeUpdate();
            System.out.println("更新了 " + rows + " 行");
            
            // 关闭连接
            pstmt.close();
            conn.close();
            
            System.out.println("密码更新成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

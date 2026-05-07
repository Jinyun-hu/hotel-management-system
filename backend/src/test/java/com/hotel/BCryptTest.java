package com.hotel;

import cn.hutool.crypto.digest.BCrypt;
import org.junit.jupiter.api.Test;

public class BCryptTest {

    @Test
    public void testBCrypt() {
        String password = "123456";
        String hash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH";

        // 验证密码
        boolean result = BCrypt.checkpw(password, hash);
        System.out.println("密码验证结果: " + result);

        // 生成新的哈希
        String newHash = BCrypt.hashpw(password);
        System.out.println("新生成的哈希: " + newHash);

        // 验证新生成的哈希
        boolean newResult = BCrypt.checkpw(password, newHash);
        System.out.println("新哈希验证结果: " + newResult);
    }
}

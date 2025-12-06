package com.nexus.system.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 密码加密工具类测试
 */
class PasswordUtilTest {

    @Test
    void testEncode() {
        // 测试密码加密
        String rawPassword = "password123";
        String encodedPassword = PasswordUtil.encode(rawPassword);
        
        // 验证加密后的密码不为空且不等于原始密码
        assertNotNull(encodedPassword);
        assertNotEquals(rawPassword, encodedPassword);
        assertTrue(encodedPassword.length() > 0);
        
        // 验证每次加密结果不同（因为BCrypt使用随机盐）
        String encodedPassword2 = PasswordUtil.encode(rawPassword);
        assertNotEquals(encodedPassword, encodedPassword2);
    }

    @Test
    void testMatches_Success() {
        // 测试密码匹配
        String rawPassword = "password123";
        String encodedPassword = PasswordUtil.encode(rawPassword);
        
        // 验证匹配成功
        boolean matches = PasswordUtil.matches(rawPassword, encodedPassword);
        assertTrue(matches);
    }

    @Test
    void testMatches_Failed() {
        // 测试密码不匹配
        String rawPassword = "password123";
        String wrongPassword = "wrongpassword";
        String encodedPassword = PasswordUtil.encode(rawPassword);
        
        // 验证匹配失败
        boolean matches = PasswordUtil.matches(wrongPassword, encodedPassword);
        assertFalse(matches);
    }

    @Test
    void testEncodeAndMatches_Consistency() {
        // 测试加密和验证的一致性
        String[] passwords = {"password123", "admin@123", "test", "!@#$%^&*()"};
        
        for (String password : passwords) {
            String encoded = PasswordUtil.encode(password);
            assertTrue(PasswordUtil.matches(password, encoded));
            assertFalse(PasswordUtil.matches(password + "wrong", encoded));
        }
    }

    @Test
    void testEncode_EmptyPassword() {
        // 测试空密码
        String encoded = PasswordUtil.encode("");
        assertNotNull(encoded);
        assertTrue(PasswordUtil.matches("", encoded));
    }

    @Test
    void testEncode_SpecialCharacters() {
        // 测试特殊字符密码
        String password = "!@#$%^&*()_+-=[]{}|;:,.<>?";
        String encoded = PasswordUtil.encode(password);
        assertTrue(PasswordUtil.matches(password, encoded));
    }
}

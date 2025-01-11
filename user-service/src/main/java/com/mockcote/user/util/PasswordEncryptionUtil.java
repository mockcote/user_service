package com.mockcote.user.util;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncryptionUtil {
	private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 비밀번호 암호화
    public static String encryptPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    // 비밀번호 검증
    public static boolean isPasswordMatch(String rawPassword, String encryptedPassword) {
        return passwordEncoder.matches(rawPassword, encryptedPassword);
    }
}

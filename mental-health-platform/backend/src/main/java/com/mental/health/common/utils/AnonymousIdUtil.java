package com.mental.health.common.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * 匿名ID生成工具类
 * 规则：anonymous_yyyyMMdd_随机6位
 * 同一用户同一天生成相同的匿名ID
 */
public class AnonymousIdUtil {

    private static final String PREFIX = "anonymous_";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final Random RANDOM = new Random();

    /**
     * 生成匿名ID
     * @param userId 用户ID
     * @return 匿名ID
     */
    public static String generateAnonymousId(Long userId) {
        String date = LocalDate.now().format(DATE_FORMATTER);
        // 使用用户ID和日期作为种子，确保同一天同一用户生成相同的随机数
        long seed = userId + Long.parseLong(date);
        Random random = new Random(seed);
        
        // 生成6位随机数字
        int randomNum = 100000 + random.nextInt(900000);
        
        return PREFIX + date + "_" + randomNum;
    }

    /**
     * 验证匿名ID格式
     */
    public static boolean isValidAnonymousId(String anonymousId) {
        if (anonymousId == null || anonymousId.isEmpty()) {
            return false;
        }
        return anonymousId.matches("^anonymous_\\d{8}_\\d{6}$");
    }
}

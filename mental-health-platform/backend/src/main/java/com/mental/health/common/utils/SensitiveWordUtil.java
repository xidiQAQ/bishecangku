package com.mental.health.common.utils;

import lombok.extern.slf4j.Slf4j;
import java.util.*;

/**
 * 敏感词过滤工具类 - DFA算法实现
 */
@Slf4j
public class SensitiveWordUtil {

    /**
     * 敏感词库 - DFA算法的核心数据结构
     */
    private static Map<String, Object> sensitiveWordMap = new HashMap<>();

    /**
     * 是否结束标识
     */
    private static final String IS_END = "isEnd";

    /**
     * 初始化敏感词库
     */
    public static void initSensitiveWordMap(Set<String> sensitiveWordSet) {
        if (sensitiveWordSet == null || sensitiveWordSet.isEmpty()) {
            log.warn("敏感词库为空");
            return;
        }

        Map<String, Object> newWordMap = new HashMap<>(sensitiveWordSet.size());
        
        for (String word : sensitiveWordSet) {
            if (word == null || word.trim().isEmpty()) {
                continue;
            }
            
            Map<String, Object> nowMap = newWordMap;
            for (int i = 0; i < word.length(); i++) {
                String keyChar = String.valueOf(word.charAt(i));
                
                Object wordMap = nowMap.get(keyChar);
                if (wordMap != null) {
                    nowMap = (Map<String, Object>) wordMap;
                } else {
                    Map<String, Object> newMap = new HashMap<>();
                    newMap.put(IS_END, "0");
                    nowMap.put(keyChar, newMap);
                    nowMap = newMap;
                }
                
                if (i == word.length() - 1) {
                    nowMap.put(IS_END, "1");
                }
            }
        }
        
        sensitiveWordMap = newWordMap;
        log.info("敏感词库初始化完成，共加载{}个敏感词", sensitiveWordSet.size());
    }

    /**
     * 检查文本中是否包含敏感词
     */
    public static boolean containsSensitiveWord(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }
        
        for (int i = 0; i < text.length(); i++) {
            int length = checkSensitiveWord(text, i);
            if (length > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取文本中的所有敏感词
     */
    public static Set<String> getSensitiveWords(String text) {
        Set<String> sensitiveWords = new HashSet<>();
        if (text == null || text.trim().isEmpty()) {
            return sensitiveWords;
        }
        
        for (int i = 0; i < text.length(); i++) {
            int length = checkSensitiveWord(text, i);
            if (length > 0) {
                sensitiveWords.add(text.substring(i, i + length));
                i = i + length - 1;
            }
        }
        return sensitiveWords;
    }

    /**
     * 替换敏感词
     */
    public static String replaceSensitiveWord(String text, String replaceChar) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }
        
        StringBuilder result = new StringBuilder(text);
        Set<String> sensitiveWords = getSensitiveWords(text);
        
        for (String word : sensitiveWords) {
            String replacement = replaceChar.repeat(word.length());
            int index = result.indexOf(word);
            while (index != -1) {
                result.replace(index, index + word.length(), replacement);
                index = result.indexOf(word, index + replacement.length());
            }
        }
        
        return result.toString();
    }

    /**
     * 检查从指定位置开始是否存在敏感词
     * @return 敏感词长度，0表示不存在
     */
    private static int checkSensitiveWord(String text, int beginIndex) {
        boolean flag = false;
        int matchLength = 0;
        
        Map<String, Object> nowMap = sensitiveWordMap;
        for (int i = beginIndex; i < text.length(); i++) {
            String word = String.valueOf(text.charAt(i));
            nowMap = (Map<String, Object>) nowMap.get(word);
            
            if (nowMap != null) {
                matchLength++;
                if ("1".equals(nowMap.get(IS_END))) {
                    flag = true;
                    break;
                }
            } else {
                break;
            }
        }
        
        if (!flag) {
            matchLength = 0;
        }
        return matchLength;
    }

    /**
     * 清空敏感词库
     */
    public static void clearSensitiveWordMap() {
        sensitiveWordMap.clear();
    }
}

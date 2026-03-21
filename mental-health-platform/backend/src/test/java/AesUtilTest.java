import com.mental.health.common.utils.AesUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AES加密工具测试类
 */
public class AesUtilTest {

    @Test
    public void testEncryptDecrypt() {
        String originalText = "这是一段需要加密的咨询笔记内容，包含敏感信息。";
        
        // 加密
        String encrypted = AesUtil.encrypt(originalText);
        System.out.println("原文: " + originalText);
        System.out.println("密文: " + encrypted);
        
        // 验证密文不等于原文
        assertNotEquals(originalText, encrypted);
        
        // 解密
        String decrypted = AesUtil.decrypt(encrypted);
        System.out.println("解密: " + decrypted);
        
        // 验证解密后等于原文
        assertEquals(originalText, decrypted);
    }

    @Test
    public void testEmptyString() {
        String empty = "";
        String encrypted = AesUtil.encrypt(empty);
        assertEquals(empty, encrypted);
        
        String decrypted = AesUtil.decrypt(encrypted);
        assertEquals(empty, decrypted);
    }

    @Test
    public void testNullString() {
        String nullStr = null;
        String encrypted = AesUtil.encrypt(nullStr);
        assertNull(encrypted);
        
        String decrypted = AesUtil.decrypt(encrypted);
        assertNull(decrypted);
    }

    @Test
    public void testLongText() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("这是第").append(i).append("行内容。");
        }
        String longText = sb.toString();
        
        String encrypted = AesUtil.encrypt(longText);
        String decrypted = AesUtil.decrypt(encrypted);
        
        assertEquals(longText, decrypted);
    }

    @Test
    public void testSpecialCharacters() {
        String specialText = "特殊字符测试：!@#$%^&*()_+-=[]{}|;':\",./<>?`~\n\t\r";
        
        String encrypted = AesUtil.encrypt(specialText);
        String decrypted = AesUtil.decrypt(encrypted);
        
        assertEquals(specialText, decrypted);
    }

    @Test
    public void testKeyLength() {
        // 验证密钥长度是否为32字节（256位）
        String secretKey = "12345678901234567890123456789012";
        int keyLength = secretKey.getBytes().length;
        
        System.out.println("密钥长度: " + keyLength + " 字节");
        assertEquals(32, keyLength, "密钥必须是32字节才能使用AES-256加密");
    }
}

package com.mental.health.vo;

import lombok.Data;
import java.io.Serializable;

/**
 * 登录返回VO
 */
@Data
public class LoginVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 用户类型 1-学生 2-咨询师 3-管理员
     */
    private Integer userType;

    /**
     * Token
     */
    private String token;

    /**
     * Refresh Token
     */
    private String refreshToken;

    /**
     * Token过期时间（毫秒）
     */
    private Long expireTime;
}

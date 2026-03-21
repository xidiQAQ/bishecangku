package com.mental.health.service;

import com.mental.health.dto.LoginDTO;
import com.mental.health.dto.RegisterDTO;
import com.mental.health.vo.LoginVO;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户登录
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 用户注册
     */
    void register(RegisterDTO registerDTO);

    /**
     * 刷新Token
     */
    LoginVO refreshToken(String refreshToken);
}

package com.mental.health.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mental.health.common.utils.JwtUtil;
import com.mental.health.dto.LoginDTO;
import com.mental.health.dto.RegisterDTO;
import com.mental.health.entity.SysUser;
import com.mental.health.mapper.SysUserMapper;
import com.mental.health.service.AuthService;
import com.mental.health.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现类
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.expiration}")
    private Long expiration;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        // 查询用户
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, loginDTO.getUsername());
        SysUser user = sysUserMapper.selectOne(wrapper);

        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }

        // 生成Access Token和Refresh Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getUserType());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getUsername(), user.getUserType());

        // 构造返回对象
        LoginVO loginVO = new LoginVO();
        loginVO.setId(user.getId());
        loginVO.setUsername(user.getUsername());
        loginVO.setRealName(user.getRealName());
        loginVO.setUserType(user.getUserType());
        loginVO.setToken(token);
        loginVO.setRefreshToken(refreshToken);
        loginVO.setExpireTime(expiration);

        return loginVO;
    }

    @Override
    public void register(RegisterDTO registerDTO) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, registerDTO.getUsername());
        Long count = sysUserMapper.selectCount(wrapper);
        if (count > 0) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查手机号是否已存在
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getPhone, registerDTO.getPhone());
        count = sysUserMapper.selectCount(wrapper);
        if (count > 0) {
            throw new RuntimeException("手机号已被注册");
        }

        // 创建用户
        SysUser user = new SysUser();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setRealName(registerDTO.getRealName());
        user.setPhone(registerDTO.getPhone());
        user.setEmail(registerDTO.getEmail());
        user.setUserType(registerDTO.getUserType());
        user.setStatus(1);
        user.setGender(0);

        sysUserMapper.insert(user);
    }

    @Override
    public LoginVO refreshToken(String refreshToken) {
        // 验证Refresh Token
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new RuntimeException("Refresh Token无效或已过期");
        }

        // 从Refresh Token中获取用户信息
        Long userId = jwtUtil.getUserIdFromToken(refreshToken);
        String username = jwtUtil.getUsernameFromToken(refreshToken);
        Integer userType = jwtUtil.getUserTypeFromToken(refreshToken);

        if (userId == null || username == null || userType == null) {
            throw new RuntimeException("Refresh Token信息不完整");
        }

        // 查询用户，确保用户仍然有效
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }

        // 生成新的Access Token和Refresh Token
        String newToken = jwtUtil.generateToken(userId, username, userType);
        String newRefreshToken = jwtUtil.generateRefreshToken(userId, username, userType);

        // 构造返回对象
        LoginVO loginVO = new LoginVO();
        loginVO.setId(userId);
        loginVO.setUsername(username);
        loginVO.setRealName(user.getRealName());
        loginVO.setUserType(userType);
        loginVO.setToken(newToken);
        loginVO.setRefreshToken(newRefreshToken);
        loginVO.setExpireTime(expiration);

        return loginVO;
    }
}

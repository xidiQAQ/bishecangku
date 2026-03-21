package com.mental.health.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CounselorDTO {
    
    @NotBlank(message = "姓名不能为空")
    private String realName;
    
    @NotBlank(message = "手机号不能为空")
    private String phone;
    
    private String username;
    
    private String password;
    
    private String title;
    
    private String specialty;
    
    private String introduction;
    
    private String education;
    
    private String certificate;
}

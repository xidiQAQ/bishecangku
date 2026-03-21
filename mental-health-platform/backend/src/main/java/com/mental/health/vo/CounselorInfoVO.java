package com.mental.health.vo;

import lombok.Data;

@Data
public class CounselorInfoVO {
    private Long id;
    private Long userId;
    private String realName;
    private String title;
    private String specialty;
    private String introduction;
    private String education;
    private String experience;
    private String certificate;
    private Double rating;
    private Integer ratingCount;
    private Integer appointmentCount;
    private Integer completedCount;
}

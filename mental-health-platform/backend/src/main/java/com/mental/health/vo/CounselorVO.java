package com.mental.health.vo;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 咨询师VO
 */
@Data
public class CounselorVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private String realName;
    private String avatar;
    private String title;
    private String specialty;
    private String introduction;
    private String education;
    private String experience;
    private BigDecimal rating;
    private Integer ratingCount;
    private Integer appointmentCount;
    private Integer completedCount;
}

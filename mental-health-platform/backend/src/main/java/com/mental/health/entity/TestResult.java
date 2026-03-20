package com.mental.health.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("test_result")
public class TestResult implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long userId;
    private Long testId;
    private String answers;
    private Integer totalScore;
    private String resultLevel;
    private String resultAnalysis;
    private String suggestions;
    private Integer testDuration;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}

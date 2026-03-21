package com.mental.health.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
public class ScheduleDTO {
    
    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;
    
    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;
    
    @NotNull(message = "时间段不能为空")
    private List<String> timeSlots;
    
    private List<Integer> weekdays; // 1-7 表示周一到周日
}

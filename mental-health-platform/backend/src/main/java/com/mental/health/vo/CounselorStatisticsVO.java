package com.mental.health.vo;

import lombok.Data;

import java.util.List;

@Data
public class CounselorStatisticsVO {
    private Integer totalAppointments;
    private Integer completedAppointments;
    private Integer monthAppointments;
    private Double averageRating;
    private Integer ratingCount;
    private List<RatingTrendVO> ratingTrend;
    
    @Data
    public static class RatingTrendVO {
        private String date;
        private Double rating;
    }
}

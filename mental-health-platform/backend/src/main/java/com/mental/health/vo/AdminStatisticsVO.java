package com.mental.health.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("管理后台统计数据VO")
public class AdminStatisticsVO {

    @ApiModelProperty("用户总数")
    private Long totalUsers;

    @ApiModelProperty("学生总数")
    private Long totalStudents;

    @ApiModelProperty("咨询师总数")
    private Long totalCounselors;

    @ApiModelProperty("文章总数")
    private Long totalArticles;

    @ApiModelProperty("预约总数")
    private Long totalAppointments;

    @ApiModelProperty("已完成预约数")
    private Long completedAppointments;

    @ApiModelProperty("测试总数")
    private Long totalTests;

    @ApiModelProperty("树洞总数")
    private Long totalMoments;

    @ApiModelProperty("待审核树洞数")
    private Long pendingMoments;

    @ApiModelProperty("今日新增用户")
    private Long todayNewUsers;

    @ApiModelProperty("今日新增预约")
    private Long todayNewAppointments;

    @ApiModelProperty("今日新增树洞")
    private Long todayNewMoments;
}

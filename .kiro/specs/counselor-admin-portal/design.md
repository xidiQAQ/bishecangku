# 设计文档

## 概述

本设计文档描述了大学生心理健康关怀与预约平台的咨询师端和管理后台功能的技术实现方案。系统基于已有的SpringBoot后端和Vue3前端架构,补充咨询师端的完整功能模块和管理后台的前端页面实现。

### 功能范围

咨询师端功能:
- 时间表管理: 设置和管理可预约时间段
- 预约管理: 查看、确认、拒绝、完成预约
- 咨询笔记: 创建和管理加密的咨询记录
- 个人中心: 管理个人信息和查看工作统计

管理后台功能:
- 咨询师管理: 添加、编辑、删除咨询师账号
- 内容管理: 文章管理、树洞审核、敏感词维护
- 用户管理: 学生用户管理和状态控制
- 数据统计: 平台运营数据可视化

### 技术栈

后端:
- Spring Boot 2.7.18
- MyBatis Plus 3.5.x
- Spring Security + JWT
- MySQL 8.0
- Redis 6.x
- AES-256加密

前端:
- Vue 3.3.4 (Composition API)
- Element Plus 2.x
- Pinia状态管理
- Axios HTTP客户端
- ECharts 5.x图表库

## 架构设计

### 系统架构

系统采用前后端分离架构,分为三个独立的前端应用:

```
┌─────────────────────────────────────────────────────────┐
│                      前端应用层                          │
├─────────────────┬─────────────────┬─────────────────────┤
│   学生端应用     │   咨询师端应用   │   管理后台应用       │
│   (已完成)      │   (待开发)      │   (待开发)          │
└─────────────────┴─────────────────┴─────────────────────┘
                          │
                          ↓
┌─────────────────────────────────────────────────────────┐
│                    API网关层 (Nginx)                     │
└─────────────────────────────────────────────────────────┘
                          │
                          ↓
┌─────────────────────────────────────────────────────────┐
│                   Spring Boot应用层                      │
├─────────────────┬─────────────────┬─────────────────────┤
│  Controller层   │   Service层     │    Mapper层         │
└─────────────────┴─────────────────┴─────────────────────┘
                          │
                          ↓
┌─────────────────────────────────────────────────────────┐
│                      数据层                              │
├─────────────────────────┬───────────────────────────────┤
│      MySQL 8.0          │        Redis 6.x              │
└─────────────────────────┴───────────────────────────────┘
```

### 模块划分

后端模块:
- 时间表服务 (ScheduleService): 管理咨询师时间表
- 预约服务 (AppointmentService): 处理预约业务逻辑
- 笔记服务 (CounselingNoteService): 管理加密的咨询笔记
- 审核服务 (AuditService): 处理树洞内容审核
- 统计服务 (StatisticsService): 生成数据统计报表
- 敏感词过滤器 (SensitiveWordFilter): DFA算法过滤敏感词

前端模块:
- 咨询师端路由模块
- 管理后台路由模块
- 共享组件库
- API接口封装
- 状态管理

## 组件和接口

### 后端组件

#### 1. 时间表服务 (ScheduleService)

```java
public interface ScheduleService {
    // 批量创建时间表
    void batchCreateSchedule(Long counselorId, LocalDate startDate, 
                            LocalDate endDate, List<String> timeSlots);
    
    // 获取咨询师时间表
    List<CounselorSchedule> getSchedule(Long counselorId, 
                                       LocalDate startDate, LocalDate endDate);
    
    // 更新时间段状态
    void updateScheduleStatus(Long scheduleId, Integer status);
    
    // 删除时间段
    void deleteSchedule(Long scheduleId);
}
```

#### 2. 咨询笔记服务 (CounselingNoteService)

```java
public interface CounselingNoteService {
    // 创建笔记(自动加密)
    void createNote(CounselingNoteDTO dto, Long counselorId);
    
    // 获取笔记列表(自动解密)
    Page<CounselingNoteVO> getNoteList(Long counselorId, String studentName, 
                                       Integer current, Integer size);
    
    // 获取笔记详情(自动解密)
    CounselingNoteVO getNoteDetail(Long noteId, Long counselorId);
    
    // 更新笔记(自动加密)
    void updateNote(Long noteId, CounselingNoteDTO dto, Long counselorId);
}
```

#### 3. 审核服务 (AuditService)

```java
public interface AuditService {
    // 获取待审核列表
    Page<MomentVO> getPendingList(Integer current, Integer size);
    
    // 审核通过
    void approve(Long momentId, Long auditorId);
    
    // 审核拒绝
    void reject(Long momentId, Long auditorId, String reason);
    
    // 检测敏感词
    SensitiveWordResult detectSensitiveWords(String content);
}
```

#### 4. 敏感词过滤器 (SensitiveWordFilter)

```java
public class SensitiveWordFilter {
    // DFA算法检测敏感词
    public SensitiveWordResult filter(String content);
    
    // 重新加载敏感词库
    public void reload();
    
    // 高亮敏感词
    public String highlight(String content);
}
```

### API接口设计

#### 咨询师端接口

```
# 时间表管理
POST   /api/counselor/schedule/batch          # 批量创建时间表
GET    /api/counselor/schedule                # 获取时间表
PUT    /api/counselor/schedule/{id}/status    # 更新时间段状态
DELETE /api/counselor/schedule/{id}           # 删除时间段

# 预约管理
GET    /api/counselor/appointments            # 获取预约列表
PUT    /api/counselor/appointments/{id}/confirm  # 确认预约
PUT    /api/counselor/appointments/{id}/reject   # 拒绝预约
PUT    /api/counselor/appointments/{id}/complete # 完成预约

# 咨询笔记
POST   /api/counselor/notes                   # 创建笔记
GET    /api/counselor/notes                   # 获取笔记列表
GET    /api/counselor/notes/{id}              # 获取笔记详情
PUT    /api/counselor/notes/{id}              # 更新笔记

# 个人中心
GET    /api/counselor/profile                 # 获取个人信息
PUT    /api/counselor/profile                 # 更新个人信息
GET    /api/counselor/statistics              # 获取工作统计
```

#### 管理后台接口

```
# 咨询师管理
GET    /api/admin/counselors                  # 获取咨询师列表
POST   /api/admin/counselors                  # 添加咨询师
PUT    /api/admin/counselors/{id}             # 更新咨询师
DELETE /api/admin/counselors/{id}             # 删除咨询师
PUT    /api/admin/counselors/{id}/status      # 启用/停用咨询师

# 内容管理
GET    /api/admin/articles                    # 获取文章列表
POST   /api/admin/articles                    # 发布文章
PUT    /api/admin/articles/{id}               # 更新文章
DELETE /api/admin/articles/{id}               # 删除文章

GET    /api/admin/moments/pending             # 获取待审核树洞
PUT    /api/admin/moments/{id}/approve        # 审核通过
PUT    /api/admin/moments/{id}/reject         # 审核拒绝

GET    /api/admin/sensitive-words             # 获取敏感词列表
POST   /api/admin/sensitive-words             # 添加敏感词
PUT    /api/admin/sensitive-words/{id}        # 更新敏感词
DELETE /api/admin/sensitive-words/{id}        # 删除敏感词
POST   /api/admin/sensitive-words/reload      # 重新加载敏感词库

# 用户管理
GET    /api/admin/users                       # 获取用户列表
PUT    /api/admin/users/{id}/status           # 启用/禁用用户

# 数据统计
GET    /api/admin/statistics/overview         # 获取概览数据
GET    /api/admin/statistics/appointments     # 获取预约统计
GET    /api/admin/statistics/counselors       # 获取咨询师统计
GET    /api/admin/statistics/articles         # 获取文章统计
```

### 前端组件

#### 咨询师端页面组件

```
views/counselor/
├── Layout.vue                    # 咨询师端布局
├── Dashboard.vue                 # 工作台
├── Schedule.vue                  # 时间管理
├── Appointments.vue              # 预约管理
├── Notes.vue                     # 咨询笔记
└── Profile.vue                   # 个人中心

components/counselor/
├── ScheduleCalendar.vue          # 时间表日历组件
├── AppointmentCard.vue           # 预约卡片组件
├── NoteEditor.vue                # 笔记编辑器组件
└── StatisticsChart.vue           # 统计图表组件
```

#### 管理后台页面组件

```
views/admin/
├── Layout.vue                    # 管理后台布局
├── Dashboard.vue                 # 数据看板
├── Counselors.vue                # 咨询师管理
├── Articles.vue                  # 文章管理
├── Moments.vue                   # 树洞审核
├── Users.vue                     # 用户管理
├── SensitiveWords.vue            # 敏感词管理
└── Statistics.vue                # 数据统计

components/admin/
├── CounselorForm.vue             # 咨询师表单组件
├── ArticleEditor.vue             # 文章编辑器组件
├── MomentAuditCard.vue           # 树洞审核卡片
├── StatisticsOverview.vue        # 统计概览组件
└── ChartPanel.vue                # 图表面板组件
```

## 数据模型

### 核心实体

#### CounselorSchedule (咨询师时间表)

```java
@Data
@TableName("counselor_schedule")
public class CounselorSchedule {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private Long counselorId;        // 咨询师ID
    private LocalDate scheduleDate;  // 日期
    private String timeSlot;         // 时间段 "HH:mm-HH:mm"
    private Integer status;          // 0-休息 1-可预约 2-已预约 3-已完成
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
```

#### CounselingNote (咨询笔记)

```java
@Data
@TableName("counseling_note")
public class CounselingNote {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private Long appointmentId;      // 预约ID
    private Long counselorId;        // 咨询师ID
    private Long studentId;          // 学生ID
    
    // 以下字段使用AES-256加密存储
    private String noteContent;      // 笔记内容(加密)
    private String problemAnalysis;  // 问题分析(加密)
    private String counselingPlan;   // 咨询方案(加密)
    private String followUpPlan;     // 跟进计划(加密)
    
    private LocalDate nextAppointment; // 下次预约建议日期
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableLogic
    private Integer deleted;
}
```

#### SensitiveWord (敏感词)

```java
@Data
@TableName("sensitive_word")
public class SensitiveWord {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private String word;             // 敏感词
    private Integer level;           // 1-警告 2-拦截
    private String category;         // 分类
    private Integer status;          // 0-禁用 1-启用
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
```

### 数据传输对象 (DTO)

#### ScheduleBatchDTO

```java
@Data
public class ScheduleBatchDTO {
    @NotNull
    private LocalDate startDate;
    
    @NotNull
    private LocalDate endDate;
    
    @NotEmpty
    private List<String> timeSlots;  // ["09:00-10:00", "10:00-11:00"]
    
    private List<Integer> weekdays;  // [1,2,3,4,5] 周一到周五
}
```

#### CounselingNoteDTO

```java
@Data
public class CounselingNoteDTO {
    @NotNull
    private Long appointmentId;
    
    @NotBlank
    private String noteContent;
    
    private String problemAnalysis;
    private String counselingPlan;
    private String followUpPlan;
    private LocalDate nextAppointment;
}
```

#### SensitiveWordDTO

```java
@Data
public class SensitiveWordDTO {
    @NotBlank
    private String word;
    
    @NotNull
    private Integer level;  // 1-警告 2-拦截
    
    private String category;
}
```

### 视图对象 (VO)

#### CounselorStatisticsVO

```java
@Data
public class CounselorStatisticsVO {
    private Integer totalAppointments;    // 预约总数
    private Integer completedCount;       // 完成数
    private Integer monthAppointments;    // 本月预约数
    private BigDecimal rating;            // 评分
    private Integer ratingCount;          // 评价数
    private List<RatingTrendVO> ratingTrend; // 评分趋势
}
```

#### AdminStatisticsVO

```java
@Data
public class AdminStatisticsVO {
    private Integer totalUsers;           // 总用户数
    private Integer totalAppointments;    // 总预约数
    private Integer monthAppointments;    // 本月预约数
    private Integer pendingMoments;       // 待审核树洞数
    private List<AppointmentTrendVO> appointmentTrend; // 预约趋势
    private BigDecimal completionRate;    // 完成率
}
```


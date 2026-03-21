<template>
  <div class="statistics-container">
    <el-card class="overview-card">
      <div class="card-header">
        <h3>数据概览</h3>
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          @change="handleDateChange"
        />
      </div>
      <div class="overview-grid">
        <div class="overview-item">
          <div class="item-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
            <el-icon><User /></el-icon>
          </div>
          <div class="item-content">
            <div class="item-value">{{ stats.totalUsers }}</div>
            <div class="item-label">总用户数</div>
          </div>
        </div>
        <div class="overview-item">
          <div class="item-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
            <el-icon><Calendar /></el-icon>
          </div>
          <div class="item-content">
            <div class="item-value">{{ stats.totalAppointments }}</div>
            <div class="item-label">总预约数</div>
          </div>
        </div>
        <div class="overview-item">
          <div class="item-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
            <el-icon><Document /></el-icon>
          </div>
          <div class="item-content">
            <div class="item-value">{{ stats.totalArticles }}</div>
            <div class="item-label">文章总数</div>
          </div>
        </div>
        <div class="overview-item">
          <div class="item-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)">
            <el-icon><ChatDotRound /></el-icon>
          </div>
          <div class="item-content">
            <div class="item-value">{{ stats.totalMoments }}</div>
            <div class="item-label">树洞总数</div>
          </div>
        </div>
      </div>
    </el-card>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-card class="chart-card">
          <div class="card-header">
            <h3>预约趋势</h3>
          </div>
          <div ref="appointmentChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <div class="card-header">
            <h3>预约完成率</h3>
          </div>
          <div ref="completionChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-card class="chart-card">
          <div class="card-header">
            <h3>咨询师工作量排行</h3>
          </div>
          <div ref="counselorChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <div class="card-header">
            <h3>用户活跃度</h3>
          </div>
          <div ref="activityChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Calendar, Document, ChatDotRound } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import adminApi from '@/api/admin'

const dateRange = ref([])
const appointmentChartRef = ref(null)
const completionChartRef = ref(null)
const counselorChartRef = ref(null)
const activityChartRef = ref(null)

let appointmentChart = null
let completionChart = null
let counselorChart = null
let activityChart = null

const stats = reactive({
  totalUsers: 0,
  totalAppointments: 0,
  totalArticles: 0,
  totalMoments: 0
})

onMounted(async () => {
  await loadStats()
  await nextTick()
  initCharts()
})

const loadStats = async () => {
  try {
    const res = await adminApi.getStatistics()
    Object.assign(stats, res.data)
  } catch (error) {
    ElMessage.error('加载统计数据失败')
  }
}

const handleDateChange = () => {
  loadStats()
  updateCharts()
}

const initCharts = () => {
  initAppointmentChart()
  initCompletionChart()
  initCounselorChart()
  initActivityChart()
}

const initAppointmentChart = () => {
  appointmentChart = echarts.init(appointmentChartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: {
      type: 'value'
    },
    series: [{
      data: [120, 132, 101, 134, 90, 230, 210],
      type: 'line',
      smooth: true,
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(102, 126, 234, 0.5)' },
          { offset: 1, color: 'rgba(102, 126, 234, 0.1)' }
        ])
      },
      itemStyle: {
        color: '#667eea'
      }
    }]
  }
  appointmentChart.setOption(option)
}

const initCompletionChart = () => {
  completionChart = echarts.init(completionChartRef.value)
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      bottom: '5%',
      left: 'center'
    },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: false
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 16,
          fontWeight: 'bold'
        }
      },
      data: [
        { value: 335, name: '已完成', itemStyle: { color: '#43e97b' } },
        { value: 234, name: '已确认', itemStyle: { color: '#4facfe' } },
        { value: 135, name: '待确认', itemStyle: { color: '#f093fb' } },
        { value: 48, name: '已取消', itemStyle: { color: '#ff6b6b' } }
      ]
    }]
  }
  completionChart.setOption(option)
}

const initCounselorChart = () => {
  counselorChart = echarts.init(counselorChartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'value'
    },
    yAxis: {
      type: 'category',
      data: ['李老师', '王老师', '张老师', '刘老师', '陈老师']
    },
    series: [{
      type: 'bar',
      data: [120, 200, 150, 80, 70],
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
          { offset: 0, color: '#667eea' },
          { offset: 1, color: '#764ba2' }
        ])
      },
      barWidth: '60%'
    }]
  }
  counselorChart.setOption(option)
}

const initActivityChart = () => {
  activityChart = echarts.init(activityChartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['预约', '树洞', '测试']
    },
    xAxis: {
      type: 'category',
      data: ['1月', '2月', '3月', '4月', '5月', '6月']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '预约',
        type: 'bar',
        data: [120, 132, 101, 134, 90, 230],
        itemStyle: { color: '#667eea' }
      },
      {
        name: '树洞',
        type: 'bar',
        data: [220, 182, 191, 234, 290, 330],
        itemStyle: { color: '#f093fb' }
      },
      {
        name: '测试',
        type: 'bar',
        data: [150, 232, 201, 154, 190, 330],
        itemStyle: { color: '#4facfe' }
      }
    ]
  }
  activityChart.setOption(option)
}

const updateCharts = () => {
  // 根据日期范围更新图表数据
  // 这里使用模拟数据，实际应该调用API获取
}
</script>

<style scoped lang="scss">
.statistics-container {
  padding: 20px;
}

.overview-card {
  margin-bottom: 20px;
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    h3 {
      margin: 0;
      font-size: 18px;
      font-weight: 500;
    }
  }
  
  .overview-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
    
    .overview-item {
      display: flex;
      align-items: center;
      gap: 16px;
      padding: 20px;
      background: #f8f9fa;
      border-radius: 8px;
      
      .item-icon {
        width: 60px;
        height: 60px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        
        .el-icon {
          font-size: 28px;
          color: white;
        }
      }
      
      .item-content {
        flex: 1;
        
        .item-value {
          font-size: 28px;
          font-weight: bold;
          color: #333;
          margin-bottom: 4px;
        }
        
        .item-label {
          font-size: 14px;
          color: #666;
        }
      }
    }
  }
}

.chart-card {
  margin-bottom: 20px;
  
  .card-header {
    margin-bottom: 20px;
    
    h3 {
      margin: 0;
      font-size: 16px;
      font-weight: 500;
    }
  }
  
  .chart-container {
    width: 100%;
    height: 300px;
  }
}
</style>

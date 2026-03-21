<template>
  <div class="profile-page">
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="header">
              <span>个人信息</span>
              <el-button type="primary" @click="isEditing = !isEditing">
                {{ isEditing ? '取消编辑' : '编辑信息' }}
              </el-button>
            </div>
          </template>
          
          <el-form :model="profileForm" label-width="100px" :disabled="!isEditing">
            <el-form-item label="姓名">
              <el-input v-model="profileForm.realName" disabled />
            </el-form-item>
            
            <el-form-item label="职称">
              <el-input v-model="profileForm.title" placeholder="如：国家二级心理咨询师" />
            </el-form-item>
            
            <el-form-item label="擅长领域">
              <el-input
                v-model="profileForm.specialty"
                type="textarea"
                :rows="3"
                placeholder="如：焦虑情绪、抑郁情绪、人际关系"
              />
            </el-form-item>
            
            <el-form-item label="个人简介">
              <el-input
                v-model="profileForm.introduction"
                type="textarea"
                :rows="4"
                placeholder="请输入个人简介"
              />
            </el-form-item>
            
            <el-form-item label="教育背景">
              <el-input
                v-model="profileForm.education"
                type="textarea"
                :rows="3"
                placeholder="如：北京大学心理学硕士"
              />
            </el-form-item>
            
            <el-form-item label="工作经验">
              <el-input
                v-model="profileForm.experience"
                type="textarea"
                :rows="4"
                placeholder="请输入工作经验"
              />
            </el-form-item>
            
            <el-form-item label="资质证书">
              <el-input
                v-model="profileForm.certificate"
                type="textarea"
                :rows="3"
                placeholder="请输入资质证书信息"
              />
            </el-form-item>
            
            <el-form-item v-if="isEditing">
              <el-button type="primary" @click="saveProfile">保存</el-button>
              <el-button @click="isEditing = false">取消</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>工作统计</span>
          </template>
          
          <div class="stat-item">
            <div class="stat-label">总预约数</div>
            <div class="stat-value">{{ statistics.totalAppointments || 0 }}</div>
          </div>
          
          <el-divider />
          
          <div class="stat-item">
            <div class="stat-label">已完成</div>
            <div class="stat-value">{{ statistics.completedAppointments || 0 }}</div>
          </div>
          
          <el-divider />
          
          <div class="stat-item">
            <div class="stat-label">本月预约</div>
            <div class="stat-value">{{ statistics.monthAppointments || 0 }}</div>
          </div>
          
          <el-divider />
          
          <div class="stat-item">
            <div class="stat-label">平均评分</div>
            <div class="stat-value">
              <el-rate
                v-model="ratingValue"
                disabled
                show-score
                text-color="#ff9900"
              />
            </div>
          </div>
          
          <el-divider />
          
          <div class="stat-item">
            <div class="stat-label">评价数量</div>
            <div class="stat-value">{{ statistics.ratingCount || 0 }}</div>
          </div>
        </el-card>
        
        <el-card style="margin-top: 20px">
          <template #header>
            <span>学生评价</span>
          </template>
          
          <el-empty v-if="!ratings || ratings.length === 0" description="暂无评价" />
          
          <div v-else class="rating-list">
            <div v-for="rating in ratings" :key="rating.id" class="rating-item">
              <div class="rating-header">
                <span class="student-name">{{ rating.studentName }}</span>
                <el-rate v-model="rating.rating" disabled size="small" />
              </div>
              <div class="rating-comment">{{ rating.comment }}</div>
              <div class="rating-date">{{ rating.createdAt }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const isEditing = ref(false)
const profileForm = ref({
  realName: '',
  title: '',
  specialty: '',
  introduction: '',
  education: '',
  experience: '',
  certificate: ''
})
const statistics = ref({})
const ratings = ref([])

const ratingValue = computed(() => statistics.value.averageRating || 0)

const getProfile = async () => {
  try {
    const res = await request.get('/counselor/profile')
    profileForm.value = res.data
  } catch (error) {
    ElMessage.error(error.message || '获取个人信息失败')
  }
}

const saveProfile = async () => {
  try {
    await request.put('/counselor/profile', profileForm.value)
    ElMessage.success('保存成功')
    isEditing.value = false
    getProfile()
  } catch (error) {
    ElMessage.error(error.message || '保存失败')
  }
}

const getStatistics = async () => {
  try {
    const res = await request.get('/counselor/statistics')
    statistics.value = res.data
  } catch (error) {
    ElMessage.error(error.message || '获取统计数据失败')
  }
}

const getRatings = async () => {
  try {
    const res = await request.get('/appointments/my', {
      params: {
        userId: userStore.user.id,
        status: 2,
        current: 1,
        size: 10
      }
    })
    ratings.value = res.data.records.filter(item => item.rating && item.comment)
  } catch (error) {
    ElMessage.error(error.message || '获取评价失败')
  }
}

onMounted(() => {
  getProfile()
  getStatistics()
  getRatings()
})
</script>

<style scoped lang="scss">
.profile-page {
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .stat-item {
    padding: 15px 0;
    
    .stat-label {
      font-size: 14px;
      color: #909399;
      margin-bottom: 10px;
    }
    
    .stat-value {
      font-size: 24px;
      font-weight: bold;
      color: #2c3e50;
    }
  }
  
  .rating-list {
    .rating-item {
      padding: 15px 0;
      border-bottom: 1px solid #ebeef5;
      
      &:last-child {
        border-bottom: none;
      }
      
      .rating-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 10px;
        
        .student-name {
          font-weight: bold;
        }
      }
      
      .rating-comment {
        color: #606266;
        margin-bottom: 5px;
      }
      
      .rating-date {
        font-size: 12px;
        color: #909399;
      }
    }
  }
}
</style>

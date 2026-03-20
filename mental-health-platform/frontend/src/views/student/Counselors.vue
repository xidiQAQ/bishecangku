<template>
  <div class="counselors-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <h1 class="page-title">咨询师团队</h1>
      <p class="page-subtitle">专业的心理咨询师，为您提供温暖的心理支持</p>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input
        v-model="searchQuery"
        placeholder="搜索咨询师姓名或专长..."
        clearable
        @change="handleSearch"
        class="search-input"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
    </div>

    <!-- 咨询师列表 -->
    <div class="counselors-grid" v-loading="loading">
      <div
        v-for="counselor in counselors"
        :key="counselor.id"
        class="counselor-card"
        @click="goToDetail(counselor.id)"
      >
        <div class="counselor-avatar">
          <el-avatar :size="100" :src="counselor.avatar">
            {{ counselor.realName?.charAt(0) }}
          </el-avatar>
          <div class="counselor-status" :class="{ online: counselor.isAvailable }">
            {{ counselor.isAvailable ? '可预约' : '已约满' }}
          </div>
        </div>

        <div class="counselor-info">
          <h3 class="counselor-name">{{ counselor.realName }}</h3>
          <p class="counselor-title">{{ counselor.title }}</p>

          <div class="counselor-rating">
            <el-rate v-model="counselor.rating" disabled show-score />
            <span class="rating-count">({{ counselor.consultCount }}次咨询)</span>
          </div>

          <div class="counselor-specialty">
            <el-tag
              v-for="(tag, index) in counselor.specialty"
              :key="index"
              size="small"
              type="info"
            >
              {{ tag }}
            </el-tag>
          </div>

          <p class="counselor-intro">{{ counselor.introduction }}</p>

          <el-button
            type="primary"
            :disabled="!counselor.isAvailable"
            @click.stop="makeAppointment(counselor.id)"
            class="appointment-btn"
          >
            <el-icon><Calendar /></el-icon>
            立即预约
          </el-button>
        </div>
      </div>

      <el-empty v-if="!loading && counselors.length === 0" description="暂无咨询师" />
    </div>

    <!-- 分页 -->
    <div class="pagination" v-if="total > 0">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[8, 16, 24, 32]"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="fetchCounselors"
        @size-change="fetchCounselors"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, Calendar } from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()

const searchQuery = ref('')
const counselors = ref([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(8)
const total = ref(0)

const fetchCounselors = async () => {
  try {
    loading.value = true
    const res = await request.get('/appointments/counselors', {
      params: {
        current: pageNum.value,
        size: pageSize.value,
        keyword: searchQuery.value || undefined
      }
    })
    counselors.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('获取咨询师失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  fetchCounselors()
}

const goToDetail = (id) => {
  router.push(`/student/counselors/${id}`)
}

const makeAppointment = (id) => {
  router.push(`/student/counselors/${id}`)
}

onMounted(() => {
  fetchCounselors()
})
</script>

<style scoped lang="scss">
@import '@/assets/styles/variables.scss';

.counselors-page {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  text-align: center;
  margin-bottom: $spacing-xl;
}

.page-title {
  font-size: 36px;
  font-weight: 700;
  color: $text-primary;
  margin-bottom: 12px;
  background: $gradient-warm;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.page-subtitle {
  font-size: 16px;
  color: $text-secondary;
}

.search-bar {
  margin-bottom: $spacing-xl;
}

.search-input {
  max-width: 600px;
  margin: 0 auto;
}

.counselors-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $spacing-lg;
  margin-bottom: $spacing-xl;
}

.counselor-card {
  background: white;
  border-radius: $radius-lg;
  padding: $spacing-xl;
  display: flex;
  gap: $spacing-lg;
  cursor: pointer;
  transition: $transition-base;
  box-shadow: $shadow-md;

  &:hover {
    transform: translateY(-4px);
    box-shadow: $shadow-xl;
  }
}

.counselor-avatar {
  position: relative;
  flex-shrink: 0;
}

.counselor-status {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  background: $text-tertiary;
  color: white;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: $radius-full;
  white-space: nowrap;

  &.online {
    background: $success-color;
  }
}

.counselor-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.counselor-name {
  font-size: 20px;
  font-weight: 600;
  color: $text-primary;
  margin-bottom: 4px;
}

.counselor-title {
  font-size: 14px;
  color: $text-secondary;
  margin-bottom: 12px;
}

.counselor-rating {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.rating-count {
  font-size: 13px;
  color: $text-tertiary;
}

.counselor-specialty {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}

.counselor-intro {
  font-size: 14px;
  color: $text-secondary;
  line-height: 1.6;
  margin-bottom: auto;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.appointment-btn {
  margin-top: 16px;
  width: 100%;
}

.pagination {
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .counselors-grid {
    grid-template-columns: 1fr;
  }

  .counselor-card {
    flex-direction: column;
    text-align: center;
  }

  .counselor-avatar {
    margin: 0 auto;
  }
}
</style>

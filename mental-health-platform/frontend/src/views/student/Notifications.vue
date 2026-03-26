<template>
  <div class="notifications-page">
    <div class="page-header">
      <h2>通知中心</h2>
      <div class="header-actions">
        <el-button 
          v-if="unreadCount > 0" 
          type="primary" 
          size="small" 
          @click="handleMarkAllRead"
        >
          全部标为已读
        </el-button>
      </div>
    </div>

    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="全部" name="all">
        <span slot="label">
          全部
          <el-badge v-if="unreadCount > 0" :value="unreadCount" class="tab-badge" />
        </span>
      </el-tab-pane>
      <el-tab-pane label="未读" name="unread" />
      <el-tab-pane label="已读" name="read" />
    </el-tabs>

    <div v-loading="loading" class="notifications-list">
      <div v-if="notifications.length === 0" class="empty-state">
        <el-empty description="暂无通知" />
      </div>

      <div
        v-for="item in notifications"
        :key="item.id"
        class="notification-item"
        :class="{ unread: !item.isRead }"
        @click="handleNotificationClick(item)"
      >
        <div class="notification-icon">
          <el-icon :size="24">
            <Bell v-if="item.type === 'system'" />
            <Calendar v-else-if="item.type === 'appointment'" />
            <ChatDotRound v-else-if="item.type === 'comment'" />
            <InfoFilled v-else />
          </el-icon>
        </div>

        <div class="notification-content">
          <div class="notification-header">
            <h4>{{ item.title }}</h4>
            <span class="notification-time">{{ formatTime(item.createdAt) }}</span>
          </div>
          <p class="notification-text">{{ item.content }}</p>
        </div>

        <div class="notification-actions">
          <el-button
            v-if="!item.isRead"
            type="primary"
            size="small"
            text
            @click.stop="handleMarkRead(item.id)"
          >
            标为已读
          </el-button>
          <el-button
            type="danger"
            size="small"
            text
            @click.stop="handleDelete(item.id)"
          >
            删除
          </el-button>
        </div>
      </div>
    </div>

    <div v-if="total > 0" class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadNotifications"
        @current-change="loadNotifications"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Bell, Calendar, ChatDotRound, InfoFilled } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import {
  getNotifications,
  getUnreadCount,
  markAsRead,
  markAllAsRead,
  deleteNotification
} from '@/api/notification'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const activeTab = ref('all')
const notifications = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const unreadCount = ref(0)

onMounted(() => {
  loadNotifications()
  loadUnreadCount()
})

const loadNotifications = async () => {
  loading.value = true
  try {
    const params = {
      current: currentPage.value,
      size: pageSize.value
    }
    
    if (activeTab.value === 'unread') {
      params.isRead = 0
    } else if (activeTab.value === 'read') {
      params.isRead = 1
    }
    
    console.log('加载通知，参数:', params)
    const res = await getNotifications(params)
    console.log('通知响应:', res)
    notifications.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('加载通知失败，错误详情:', error)
    ElMessage.error('加载通知失败: ' + (error.message || '未知错误'))
    notifications.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const loadUnreadCount = async () => {
  try {
    const res = await getUnreadCount()
    unreadCount.value = res.data || 0
    // 同时更新store中的未读数量
    userStore.setUnreadCount(res.data || 0)
  } catch (error) {
    console.error('加载未读数量失败', error)
    // 如果接口不存在，设置为0，避免报错
    unreadCount.value = 0
    userStore.setUnreadCount(0)
  }
}

const handleTabChange = () => {
  currentPage.value = 1
  loadNotifications()
}

const handleNotificationClick = async (item) => {
  // 如果未读，先标记为已读
  if (!item.isRead) {
    await handleMarkRead(item.id, false)
  }
  
  // 根据通知类型跳转
  if (item.type === 'appointment' && item.relatedId) {
    router.push(`/student/appointments`)
  }
}

const handleMarkRead = async (id, showMessage = true) => {
  try {
    await markAsRead(id)
    if (showMessage) {
      ElMessage.success('已标记为已读')
    }
    loadNotifications()
    loadUnreadCount()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleMarkAllRead = async () => {
  try {
    await markAllAsRead()
    ElMessage.success('已全部标记为已读')
    loadNotifications()
    loadUnreadCount()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这条通知吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteNotification(id)
    ElMessage.success('删除成功')
    loadNotifications()
    loadUnreadCount()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const formatTime = (time) => {
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  
  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour
  
  if (diff < minute) {
    return '刚刚'
  } else if (diff < hour) {
    return `${Math.floor(diff / minute)}分钟前`
  } else if (diff < day) {
    return `${Math.floor(diff / hour)}小时前`
  } else if (diff < 7 * day) {
    return `${Math.floor(diff / day)}天前`
  } else {
    return date.toLocaleDateString()
  }
}
</script>

<style scoped lang="scss">
.notifications-page {
  max-width: 900px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;

  h2 {
    font-size: 24px;
    font-weight: 600;
    color: #2c3e50;
    margin: 0;
  }
}

.tab-badge {
  margin-left: 8px;
}

.notifications-list {
  min-height: 400px;
}

.notification-item {
  display: flex;
  gap: 16px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #e4e7ed;

  &:hover {
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    transform: translateY(-2px);
  }

  &.unread {
    background: #f0f9ff;
    border-color: #06b6d4;

    .notification-icon {
      color: #06b6d4;
    }
  }
}

.notification-icon {
  flex-shrink: 0;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  border-radius: 50%;
  color: #909399;
}

.notification-content {
  flex: 1;
  min-width: 0;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;

  h4 {
    font-size: 16px;
    font-weight: 600;
    color: #2c3e50;
    margin: 0;
  }
}

.notification-time {
  font-size: 12px;
  color: #909399;
  white-space: nowrap;
}

.notification-text {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  margin: 0;
  white-space: pre-wrap;
}

.notification-actions {
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: flex-end;
}

.empty-state {
  padding: 60px 0;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

@media (max-width: 768px) {
  .notification-item {
    flex-direction: column;
  }

  .notification-actions {
    flex-direction: row;
    justify-content: flex-end;
  }
}
</style>

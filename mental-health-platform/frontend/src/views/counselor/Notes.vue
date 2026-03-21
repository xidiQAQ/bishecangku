<template>
  <div class="notes-page">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card>
          <template #header>
            <div class="header">
              <span>笔记列表</span>
              <el-button type="primary" size="small" @click="showNoteDialog = true">新建笔记</el-button>
            </div>
          </template>
          
          <el-input
            v-model="searchKeyword"
            placeholder="搜索学生姓名"
            @input="searchNotes"
            style="margin-bottom: 15px"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          
          <el-menu :default-active="activeNoteId" @select="handleNoteSelect">
            <el-menu-item
              v-for="note in notes"
              :key="note.id"
              :index="note.id.toString()"
            >
              <div class="note-item">
                <div class="note-title">{{ note.studentName }}</div>
                <div class="note-date">{{ note.createdAt }}</div>
              </div>
            </el-menu-item>
          </el-menu>
        </el-card>
      </el-col>
      
      <el-col :span="16">
        <el-card v-if="currentNote">
          <template #header>
            <div class="header">
              <span>笔记详情</span>
              <el-button type="primary" size="small" @click="editNote">编辑</el-button>
            </div>
          </template>
          
          <el-descriptions :column="2" border>
            <el-descriptions-item label="学生姓名">{{ currentNote.studentName }}</el-descriptions-item>
            <el-descriptions-item label="预约编号">{{ currentNote.appointmentNo }}</el-descriptions-item>
            <el-descriptions-item label="咨询日期">{{ currentNote.appointmentDate }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ currentNote.createdAt }}</el-descriptions-item>
          </el-descriptions>
          
          <el-divider />
          
          <div class="note-content">
            <h4>笔记内容</h4>
            <div v-html="currentNote.noteContent"></div>
            
            <h4>问题分析</h4>
            <div v-html="currentNote.problemAnalysis"></div>
            
            <h4>咨询方案</h4>
            <div v-html="currentNote.counselingPlan"></div>
            
            <h4>跟进计划</h4>
            <div v-html="currentNote.followUpPlan"></div>
            
            <h4>下次预约建议</h4>
            <div>{{ currentNote.nextAppointment || '无' }}</div>
          </div>
        </el-card>
        
        <el-empty v-else description="请选择一条笔记" />
      </el-col>
    </el-row>
    
    <!-- 新建/编辑笔记对话框 -->
    <el-dialog
      v-model="showNoteDialog"
      :title="isEdit ? '编辑笔记' : '新建笔记'"
      width="800px"
      :close-on-click-modal="false"
    >
      <el-form :model="noteForm" label-width="100px">
        <el-form-item label="关联预约" v-if="!isEdit">
          <el-select v-model="noteForm.appointmentId" placeholder="请选择预约" filterable>
            <el-option
              v-for="apt in completedAppointments"
              :key="apt.id"
              :label="`${apt.studentName} - ${apt.appointmentDate} ${apt.timeSlot}`"
              :value="apt.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="笔记内容">
          <el-input v-model="noteForm.noteContent" type="textarea" :rows="4" />
        </el-form-item>
        
        <el-form-item label="问题分析">
          <el-input v-model="noteForm.problemAnalysis" type="textarea" :rows="4" />
        </el-form-item>
        
        <el-form-item label="咨询方案">
          <el-input v-model="noteForm.counselingPlan" type="textarea" :rows="4" />
        </el-form-item>
        
        <el-form-item label="跟进计划">
          <el-input v-model="noteForm.followUpPlan" type="textarea" :rows="4" />
        </el-form-item>
        
        <el-form-item label="下次预约">
          <el-date-picker
            v-model="noteForm.nextAppointment"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showNoteDialog = false">取消</el-button>
        <el-button type="primary" @click="saveNote">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const userStore = useUserStore()
const notes = ref([])
const currentNote = ref(null)
const activeNoteId = ref('')
const searchKeyword = ref('')
const showNoteDialog = ref(false)
const isEdit = ref(false)
const completedAppointments = ref([])
const noteForm = ref({
  appointmentId: null,
  noteContent: '',
  problemAnalysis: '',
  counselingPlan: '',
  followUpPlan: '',
  nextAppointment: null
})

const getNotes = async () => {
  try {
    const res = await request.get('/counseling-notes', {
      params: {
        pageNum: 1,
        pageSize: 100
      }
    })
    notes.value = res.data.records
  } catch (error) {
    ElMessage.error(error.message || '获取笔记列表失败')
  }
}

const getCompletedAppointments = async () => {
  try {
    const res = await request.get('/counselor/appointments', {
      params: {
        status: 2,
        current: 1,
        size: 100
      }
    })
    completedAppointments.value = res.data.records
  } catch (error) {
    ElMessage.error(error.message || '获取预约列表失败')
  }
}

const handleNoteSelect = async (id) => {
  activeNoteId.value = id
  try {
    const res = await request.get(`/counseling-notes/${id}`)
    currentNote.value = res.data
  } catch (error) {
    ElMessage.error(error.message || '获取笔记详情失败')
  }
}

const searchNotes = () => {
  getNotes()
}

const editNote = () => {
  isEdit.value = true
  noteForm.value = { ...currentNote.value }
  showNoteDialog.value = true
}

const saveNote = async () => {
  try {
    if (isEdit.value) {
      await request.put(`/counseling-notes/${currentNote.value.id}`, noteForm.value)
      ElMessage.success('更新成功')
    } else {
      await request.post('/counseling-notes', noteForm.value)
      ElMessage.success('创建成功')
    }
    showNoteDialog.value = false
    getNotes()
  } catch (error) {
    ElMessage.error(error.message || '保存失败')
  }
}

onMounted(() => {
  getNotes()
  getCompletedAppointments()
  
  if (route.query.appointmentId) {
    noteForm.value.appointmentId = parseInt(route.query.appointmentId)
    showNoteDialog.value = true
  }
})
</script>

<style scoped lang="scss">
.notes-page {
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .note-item {
    width: 100%;
    
    .note-title {
      font-weight: bold;
      margin-bottom: 5px;
    }
    
    .note-date {
      font-size: 12px;
      color: #909399;
    }
  }
  
  .note-content {
    h4 {
      margin-top: 20px;
      margin-bottom: 10px;
      color: #2c3e50;
    }
    
    div {
      padding: 10px;
      background-color: #f5f7fa;
      border-radius: 4px;
      min-height: 50px;
    }
  }
}
</style>

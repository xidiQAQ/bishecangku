<template>
  <div class="counselors-page">
    <el-card>
      <template #header>
        <div class="header">
          <span>咨询师管理</span>
          <el-button type="primary" @click="showDialog = true">添加咨询师</el-button>
        </div>
      </template>
      
      <el-table :data="counselors" v-loading="loading">
        <el-table-column prop="realName" label="姓名" width="120" />
        <el-table-column prop="phone" label="手机号" width="150" />
        <el-table-column prop="title" label="职称" width="200" />
        <el-table-column prop="specialty" label="擅长领域" show-overflow-tooltip />
        <el-table-column prop="rating" label="评分" width="100">
          <template #default="{ row }">
            <el-rate v-model="row.rating" disabled show-score text-color="#ff9900" />
          </template>
        </el-table-column>
        <el-table-column prop="appointmentCount" label="预约数" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="editCounselor(row)">编辑</el-button>
            <el-button
              :type="row.status === 1 ? 'danger' : 'success'"
              size="small"
              @click="toggleStatus(row)"
            >
              {{ row.status === 1 ? '停用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        @current-change="getCounselors"
        layout="total, prev, pager, next"
        style="margin-top: 20px; justify-content: center"
      />
    </el-card>
    
    <el-dialog v-model="showDialog" :title="isEdit ? '编辑咨询师' : '添加咨询师'" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="姓名">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="用户名" v-if="!isEdit">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="密码" v-if="!isEdit">
          <el-input v-model="form.password" type="password" />
        </el-form-item>
        <el-form-item label="职称">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="擅长领域">
          <el-input v-model="form.specialty" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="个人简介">
          <el-input v-model="form.introduction" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="saveCounselor">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const counselors = ref([])
const loading = ref(false)
const showDialog = ref(false)
const isEdit = ref(false)
const form = ref({})
const pagination = ref({ current: 1, size: 10, total: 0 })

const getCounselors = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/counselors', {
      params: {
        current: pagination.value.current,
        size: pagination.value.size
      }
    })
    counselors.value = res.data.records
    pagination.value.total = res.data.total
  } catch (error) {
    ElMessage.error(error.message || '获取列表失败')
  } finally {
    loading.value = false
  }
}

const editCounselor = (row) => {
  isEdit.value = true
  form.value = { ...row }
  showDialog.value = true
}

const saveCounselor = async () => {
  try {
    if (isEdit.value) {
      await request.put(`/admin/counselors/${form.value.id}`, form.value)
    } else {
      await request.post('/admin/counselors', form.value)
    }
    ElMessage.success('保存成功')
    showDialog.value = false
    getCounselors()
  } catch (error) {
    ElMessage.error(error.message || '保存失败')
  }
}

const toggleStatus = async (row) => {
  try {
    await request.put(`/admin/users/${row.id}/status`, null, {
      params: { status: row.status === 1 ? 0 : 1 }
    })
    ElMessage.success('操作成功')
    getCounselors()
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  }
}

onMounted(() => {
  getCounselors()
})
</script>

<style scoped lang="scss">
.counselors-page {
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>

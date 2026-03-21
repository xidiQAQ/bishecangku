import request from '@/utils/request'

export default {
  // 获取个人信息
  getProfile() {
    return request.get('/counselor/profile')
  },
  
  // 更新个人信息
  updateProfile(data) {
    return request.put('/counselor/profile', data)
  },
  
  // 获取统计数据
  getStatistics() {
    return request.get('/counselor/statistics')
  },
  
  // 获取时间表
  getSchedule(params) {
    return request.get('/counselor/schedule', { params })
  },
  
  // 批量设置时间表
  batchSetSchedule(data) {
    return request.post('/counselor/schedule/batch', data)
  },
  
  // 更新时间段状态
  updateScheduleStatus(scheduleId, status) {
    return request.put(`/counselor/schedule/${scheduleId}`, null, {
      params: { status }
    })
  },
  
  // 删除时间段
  deleteSchedule(scheduleId) {
    return request.delete(`/counselor/schedule/${scheduleId}`)
  },
  
  // 获取笔记列表
  getNotes(params) {
    return request.get('/counselor/notes', { params })
  },
  
  // 获取笔记详情
  getNoteDetail(id) {
    return request.get(`/counselor/notes/${id}`)
  },
  
  // 创建笔记
  createNote(data) {
    return request.post('/counselor/notes', data)
  },
  
  // 更新笔记
  updateNote(id, data) {
    return request.put(`/counselor/notes/${id}`, data)
  },
  
  // 获取预约列表
  getAppointments(params) {
    return request.get('/counselor/appointments', { params })
  },
  
  // 确认预约
  confirmAppointment(appointmentId) {
    return request.put(`/counselor/appointments/${appointmentId}/confirm`)
  },
  
  // 拒绝预约
  rejectAppointment(appointmentId, rejectReason) {
    return request.put(`/counselor/appointments/${appointmentId}/reject`, null, {
      params: { rejectReason }
    })
  },
  
  // 完成预约
  completeAppointment(appointmentId) {
    return request.put(`/counselor/appointments/${appointmentId}/complete`)
  }
}

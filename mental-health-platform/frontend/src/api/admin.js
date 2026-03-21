import request from '@/utils/request'

export default {
  // 获取统计数据
  getStatistics() {
    return request.get('/admin/statistics')
  },
  
  // 获取用户列表
  getUserList(params) {
    return request.get('/admin/users', { params })
  },
  
  // 更新用户状态
  updateUserStatus(userId, status) {
    return request.put(`/admin/users/${userId}/status`, null, {
      params: { status }
    })
  },
  
  // 获取待审核树洞
  getPendingMoments(params) {
    return request.get('/admin/moments/pending', { params })
  },
  
  // 审核树洞
  auditMoment(momentId, auditStatus, auditReason) {
    return request.put(`/admin/moments/${momentId}/audit`, null, {
      params: { auditStatus, auditReason }
    })
  },
  
  // 咨询师管理
  getCounselors(params) {
    return request.get('/admin/counselors', { params })
  },
  
  addCounselor(data) {
    return request.post('/admin/counselors', data)
  },
  
  updateCounselor(id, data) {
    return request.put(`/admin/counselors/${id}`, data)
  },
  
  deleteCounselor(id) {
    return request.delete(`/admin/counselors/${id}`)
  }
}

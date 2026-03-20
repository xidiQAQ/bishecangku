import request from '@/utils/request'

/**
 * 获取咨询师列表
 */
export function getCounselors(params) {
  return request({
    url: '/api/appointments/counselors',
    method: 'get',
    params
  })
}

/**
 * 获取咨询师详情
 */
export function getCounselorDetail(id) {
  return request({
    url: `/api/appointments/counselors/${id}`,
    method: 'get'
  })
}

/**
 * 获取咨询师时间表
 */
export function getCounselorSchedule(id, params) {
  return request({
    url: `/api/appointments/counselors/${id}/schedule`,
    method: 'get',
    params
  })
}

/**
 * 创建预约
 */
export function createAppointment(data) {
  return request({
    url: '/api/appointments',
    method: 'post',
    data
  })
}

/**
 * 获取我的预约列表
 */
export function getMyAppointments(params) {
  return request({
    url: '/api/appointments/my',
    method: 'get',
    params
  })
}

/**
 * 取消预约
 */
export function cancelAppointment(id) {
  return request({
    url: `/api/appointments/${id}/cancel`,
    method: 'put'
  })
}

/**
 * 评价预约
 */
export function rateAppointment(id, data) {
  return request({
    url: `/api/appointments/${id}/rate`,
    method: 'put',
    data
  })
}

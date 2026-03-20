import request from '@/utils/request'

/**
 * 发布树洞
 */
export function publishMoment(data) {
  return request({
    url: '/api/moments',
    method: 'post',
    data
  })
}

/**
 * 获取树洞列表
 */
export function getMomentList(params) {
  return request({
    url: '/api/moments',
    method: 'get',
    params
  })
}

/**
 * 获取树洞详情
 */
export function getMomentDetail(id) {
  return request({
    url: `/api/moments/${id}`,
    method: 'get'
  })
}

/**
 * 点赞树洞
 */
export function likeMoment(id) {
  return request({
    url: `/api/moments/${id}/like`,
    method: 'post'
  })
}

/**
 * 评论树洞
 */
export function commentMoment(data) {
  return request({
    url: '/api/moments/comments',
    method: 'post',
    data
  })
}

/**
 * 获取评论列表
 */
export function getCommentList(momentId) {
  return request({
    url: `/api/moments/${momentId}/comments`,
    method: 'get'
  })
}

/**
 * 删除树洞
 */
export function deleteMoment(id) {
  return request({
    url: `/api/moments/${id}`,
    method: 'delete'
  })
}

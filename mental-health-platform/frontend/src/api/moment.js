import request from '@/utils/request'

/**
 * 发布树洞
 */
export function publishMoment(data) {
  return request({
    url: '/moments',
    method: 'post',
    data
  })
}

/**
 * 获取树洞列表
 */
export function getMomentList(params) {
  return request({
    url: '/moments',
    method: 'get',
    params
  })
}

/**
 * 获取树洞详情
 */
export function getMomentDetail(id) {
  return request({
    url: `/moments/${id}`,
    method: 'get'
  })
}

/**
 * 点赞树洞
 */
export function likeMoment(id) {
  return request({
    url: `/moments/${id}/like`,
    method: 'post'
  })
}

/**
 * 评论树洞
 */
export function commentMoment(data) {
  return request({
    url: '/moments/comments',
    method: 'post',
    data
  })
}

/**
 * 获取评论列表
 */
export function getCommentList(momentId) {
  return request({
    url: `/moments/${momentId}/comments`,
    method: 'get'
  })
}

/**
 * 删除树洞
 */
export function deleteMoment(id) {
  return request({
    url: `/moments/${id}`,
    method: 'delete'
  })
}

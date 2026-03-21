import request from '@/utils/request'

/**
 * 获取文章分类列表
 */
export function getCategories() {
  return request({
    url: '/articles/categories',
    method: 'get'
  })
}

/**
 * 获取文章列表（分页）
 */
export function getArticleList(params) {
  return request({
    url: '/articles/page',
    method: 'get',
    params
  })
}

/**
 * 获取文章详情
 */
export function getArticleDetail(id) {
  return request({
    url: `/articles/${id}`,
    method: 'get'
  })
}

/**
 * 收藏文章
 */
export function collectArticle(id) {
  return request({
    url: `/articles/${id}/collect`,
    method: 'post'
  })
}

/**
 * 取消收藏文章
 */
export function uncollectArticle(id) {
  return request({
    url: `/articles/${id}/collect`,
    method: 'delete'
  })
}

/**
 * 创建文章（管理员）
 */
export function createArticle(data) {
  return request({
    url: '/admin/articles',
    method: 'post',
    data
  })
}

/**
 * 更新文章（管理员）
 */
export function updateArticle(id, data) {
  return request({
    url: `/admin/articles/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除文章（管理员）
 */
export function deleteArticle(id) {
  return request({
    url: `/admin/articles/${id}`,
    method: 'delete'
  })
}

export default {
  getCategories,
  getArticleList,
  getArticleDetail,
  collectArticle,
  uncollectArticle,
  createArticle,
  updateArticle,
  deleteArticle
}

import request from '@/utils/request'

/**
 * 获取文章分类列表
 */
export function getCategories() {
  return request({
    url: '/api/articles/categories',
    method: 'get'
  })
}

/**
 * 获取文章列表（分页）
 */
export function getArticleList(params) {
  return request({
    url: '/api/articles/page',
    method: 'get',
    params
  })
}

/**
 * 获取文章详情
 */
export function getArticleDetail(id) {
  return request({
    url: `/api/articles/${id}`,
    method: 'get'
  })
}

/**
 * 收藏文章
 */
export function collectArticle(id) {
  return request({
    url: `/api/articles/${id}/collect`,
    method: 'post'
  })
}

/**
 * 取消收藏文章
 */
export function uncollectArticle(id) {
  return request({
    url: `/api/articles/${id}/collect`,
    method: 'delete'
  })
}

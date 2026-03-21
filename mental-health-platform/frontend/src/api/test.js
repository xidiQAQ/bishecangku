import request from '@/utils/request'

/**
 * 获取测试列表
 */
export function getTestList() {
  return request({
    url: '/tests',
    method: 'get'
  })
}

/**
 * 获取测试题目
 */
export function getTestQuestions(testId) {
  return request({
    url: `/tests/${testId}/questions`,
    method: 'get'
  })
}

/**
 * 提交测试答案
 */
export function submitTest(data) {
  return request({
    url: '/tests/submit',
    method: 'post',
    data
  })
}

/**
 * 获取测试历史
 */
export function getTestHistory() {
  return request({
    url: '/tests/history',
    method: 'get'
  })
}

/**
 * 获取测试结果详情
 */
export function getTestResult(resultId) {
  return request({
    url: `/tests/results/${resultId}`,
    method: 'get'
  })
}

import request from '@/utils/request'

/**
 * 获取测试列表
 */
export function getTestList() {
  return request({
    url: '/api/tests',
    method: 'get'
  })
}

/**
 * 获取测试题目
 */
export function getTestQuestions(testId) {
  return request({
    url: `/api/tests/${testId}/questions`,
    method: 'get'
  })
}

/**
 * 提交测试答案
 */
export function submitTest(data) {
  return request({
    url: '/api/tests/submit',
    method: 'post',
    data
  })
}

/**
 * 获取测试历史
 */
export function getTestHistory() {
  return request({
    url: '/api/tests/history',
    method: 'get'
  })
}

/**
 * 获取测试结果详情
 */
export function getTestResult(resultId) {
  return request({
    url: `/api/tests/results/${resultId}`,
    method: 'get'
  })
}

package com.mental.health.service;

import com.mental.health.dto.TestAnswerDTO;
import com.mental.health.vo.TestQuestionVO;
import com.mental.health.vo.TestResultVO;
import com.mental.health.vo.TestVO;
import java.util.List;

public interface TestService {

    /**
     * 获取测试列表
     */
    List<TestVO> getTestList();

    /**
     * 获取测试详情（包含题目）
     */
    List<TestQuestionVO> getTestDetail(Long testId);

    /**
     * 提交测试答案
     */
    TestResultVO submitTest(Long userId, TestAnswerDTO answerDTO);

    /**
     * 获取测试历史
     */
    List<TestResultVO> getTestHistory(Long userId);

    /**
     * 获取测试结果详情
     */
    TestResultVO getTestResult(Long resultId, Long userId);
}

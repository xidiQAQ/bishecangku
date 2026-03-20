package com.mental.health.controller;

import com.mental.health.common.result.Result;
import com.mental.health.dto.TestAnswerDTO;
import com.mental.health.service.TestService;
import com.mental.health.vo.TestQuestionVO;
import com.mental.health.vo.TestResultVO;
import com.mental.health.vo.TestVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "心理测试接口")
@RestController
@RequestMapping("/api/tests")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @ApiOperation("获取测试列表")
    @GetMapping
    public Result<List<TestVO>> getTestList() {
        List<TestVO> tests = testService.getTestList();
        return Result.success(tests);
    }

    @ApiOperation("获取测试详情（包含题目）")
    @GetMapping("/{testId}/questions")
    public Result<List<TestQuestionVO>> getTestDetail(@PathVariable Long testId) {
        List<TestQuestionVO> questions = testService.getTestDetail(testId);
        return Result.success(questions);
    }

    @ApiOperation("提交测试答案")
    @PostMapping("/submit")
    public Result<TestResultVO> submitTest(
            @RequestHeader("userId") Long userId,
            @Validated @RequestBody TestAnswerDTO answerDTO) {
        TestResultVO result = testService.submitTest(userId, answerDTO);
        return Result.success(result);
    }

    @ApiOperation("获取测试历史")
    @GetMapping("/history")
    public Result<List<TestResultVO>> getTestHistory(@RequestHeader("userId") Long userId) {
        List<TestResultVO> history = testService.getTestHistory(userId);
        return Result.success(history);
    }

    @ApiOperation("获取测试结果详情")
    @GetMapping("/results/{resultId}")
    public Result<TestResultVO> getTestResult(
            @PathVariable Long resultId,
            @RequestHeader("userId") Long userId) {
        TestResultVO result = testService.getTestResult(resultId, userId);
        return Result.success(result);
    }
}

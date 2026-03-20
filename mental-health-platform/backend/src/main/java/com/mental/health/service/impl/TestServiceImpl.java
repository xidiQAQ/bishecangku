package com.mental.health.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mental.health.dto.TestAnswerDTO;
import com.mental.health.entity.PsychologicalTest;
import com.mental.health.entity.TestQuestion;
import com.mental.health.entity.TestResult;
import com.mental.health.mapper.PsychologicalTestMapper;
import com.mental.health.mapper.TestQuestionMapper;
import com.mental.health.mapper.TestResultMapper;
import com.mental.health.service.TestService;
import com.mental.health.vo.TestQuestionVO;
import com.mental.health.vo.TestResultVO;
import com.mental.health.vo.TestVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final PsychologicalTestMapper testMapper;
    private final TestQuestionMapper questionMapper;
    private final TestResultMapper resultMapper;
    private final ObjectMapper objectMapper;

    @Override
    public List<TestVO> getTestList() {
        LambdaQueryWrapper<PsychologicalTest> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PsychologicalTest::getStatus, 1)
                .orderByAsc(PsychologicalTest::getCreatedAt);
        
        List<PsychologicalTest> tests = testMapper.selectList(wrapper);
        return tests.stream().map(test -> {
            TestVO vo = new TestVO();
            BeanUtils.copyProperties(test, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<TestQuestionVO> getTestDetail(Long testId) {
        LambdaQueryWrapper<TestQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TestQuestion::getTestId, testId)
                .orderByAsc(TestQuestion::getQuestionNo);
        
        List<TestQuestion> questions = questionMapper.selectList(wrapper);
        return questions.stream().map(question -> {
            TestQuestionVO vo = new TestQuestionVO();
            vo.setId(question.getId());
            vo.setQuestionNo(question.getQuestionNo());
            vo.setQuestionText(question.getQuestionText());
            vo.setQuestionType(question.getQuestionType());
            
            try {
                List<TestQuestionVO.Option> options = objectMapper.readValue(
                    question.getOptions(), 
                    new TypeReference<List<TestQuestionVO.Option>>() {}
                );
                vo.setOptions(options);
            } catch (Exception e) {
                throw new RuntimeException("解析题目选项失败", e);
            }
            
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TestResultVO submitTest(Long userId, TestAnswerDTO answerDTO) {
        // 获取测试信息
        PsychologicalTest test = testMapper.selectById(answerDTO.getTestId());
        if (test == null) {
            throw new RuntimeException("测试不存在");
        }

        // 计算总分
        int totalScore = answerDTO.getAnswers().stream()
                .mapToInt(TestAnswerDTO.Answer::getScore)
                .sum();

        // 根据测试类型计算最终分数和结果
        String resultLevel;
        String resultAnalysis;
        String suggestions;

        if ("SAS".equals(test.getTestType())) {
            // SAS焦虑自评量表
            int finalScore = (int) (totalScore * 1.25);
            if (finalScore < 50) {
                resultLevel = "正常";
                resultAnalysis = "您的焦虑水平在正常范围内，心理状态良好。";
                suggestions = "继续保持良好的生活习惯，适当运动，保持积极心态。";
            } else if (finalScore < 60) {
                resultLevel = "轻度焦虑";
                resultAnalysis = "您存在轻度焦虑情绪，可能会感到一些紧张和不安。";
                suggestions = "建议进行适当的放松训练，如深呼吸、冥想等。如持续不适，可寻求心理咨询。";
            } else if (finalScore < 70) {
                resultLevel = "中度焦虑";
                resultAnalysis = "您的焦虑水平较高，可能影响到日常生活和学习。";
                suggestions = "建议尽快预约心理咨询师进行专业咨询，学习焦虑管理技巧。";
            } else {
                resultLevel = "重度焦虑";
                resultAnalysis = "您的焦虑水平很高，严重影响生活质量。";
                suggestions = "强烈建议立即寻求专业心理咨询或医疗帮助，必要时可考虑药物治疗配合心理治疗。";
            }
            totalScore = finalScore;
        } else if ("SDS".equals(test.getTestType())) {
            // SDS抑郁自评量表
            int finalScore = (int) (totalScore * 1.25);
            if (finalScore < 53) {
                resultLevel = "正常";
                resultAnalysis = "您的情绪状态良好，没有明显的抑郁倾向。";
                suggestions = "继续保持积极乐观的心态，多参与社交活动。";
            } else if (finalScore < 63) {
                resultLevel = "轻度抑郁";
                resultAnalysis = "您可能存在轻度抑郁情绪，偶尔会感到情绪低落。";
                suggestions = "建议增加户外活动，与朋友家人多交流。如情况持续，可寻求心理咨询。";
            } else if (finalScore < 73) {
                resultLevel = "中度抑郁";
                resultAnalysis = "您的抑郁程度较为明显，可能经常感到情绪低落、兴趣减退。";
                suggestions = "建议尽快预约心理咨询师，进行系统的心理干预和治疗。";
            } else {
                resultLevel = "重度抑郁";
                resultAnalysis = "您的抑郁程度很严重，可能严重影响日常生活。";
                suggestions = "强烈建议立即寻求专业医疗帮助，可能需要药物治疗配合心理治疗。";
            }
            totalScore = finalScore;
        } else {
            // 其他类型测试
            resultLevel = "已完成";
            resultAnalysis = "测试已完成，感谢您的参与。";
            suggestions = "如需了解更多，请咨询专业心理咨询师。";
        }

        // 保存测试结果
        TestResult result = new TestResult();
        result.setUserId(userId);
        result.setTestId(answerDTO.getTestId());
        try {
            result.setAnswers(objectMapper.writeValueAsString(answerDTO.getAnswers()));
        } catch (Exception e) {
            throw new RuntimeException("保存答案失败", e);
        }
        result.setTotalScore(totalScore);
        result.setResultLevel(resultLevel);
        result.setResultAnalysis(resultAnalysis);
        result.setSuggestions(suggestions);
        result.setTestDuration(answerDTO.getTestDuration());
        
        resultMapper.insert(result);

        // 返回结果
        TestResultVO vo = new TestResultVO();
        vo.setId(result.getId());
        vo.setTestId(test.getId());
        vo.setTestName(test.getName());
        vo.setTestType(test.getTestType());
        vo.setTotalScore(totalScore);
        vo.setResultLevel(resultLevel);
        vo.setResultAnalysis(resultAnalysis);
        vo.setSuggestions(suggestions);
        vo.setTestDuration(answerDTO.getTestDuration());
        vo.setCreatedAt(result.getCreatedAt());

        return vo;
    }

    @Override
    public List<TestResultVO> getTestHistory(Long userId) {
        LambdaQueryWrapper<TestResult> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TestResult::getUserId, userId)
                .orderByDesc(TestResult::getCreatedAt);
        
        List<TestResult> results = resultMapper.selectList(wrapper);
        return results.stream().map(result -> {
            TestResultVO vo = new TestResultVO();
            BeanUtils.copyProperties(result, vo);
            
            // 获取测试名称
            PsychologicalTest test = testMapper.selectById(result.getTestId());
            if (test != null) {
                vo.setTestName(test.getName());
                vo.setTestType(test.getTestType());
            }
            
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public TestResultVO getTestResult(Long resultId, Long userId) {
        TestResult result = resultMapper.selectById(resultId);
        if (result == null || !result.getUserId().equals(userId)) {
            throw new RuntimeException("测试结果不存在");
        }

        TestResultVO vo = new TestResultVO();
        BeanUtils.copyProperties(result, vo);
        
        // 获取测试名称
        PsychologicalTest test = testMapper.selectById(result.getTestId());
        if (test != null) {
            vo.setTestName(test.getName());
            vo.setTestType(test.getTestType());
        }

        return vo;
    }
}

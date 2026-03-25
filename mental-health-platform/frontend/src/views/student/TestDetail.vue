<template>
  <div class="test-detail-page">
    <div v-if="!testStarted" class="test-intro">
      <div class="intro-card">
        <h1 class="test-title">{{ testInfo.name }}</h1>
        <p class="test-description">{{ testInfo.description }}</p>
        
        <div class="test-info">
          <div class="info-item">
            <el-icon><Document /></el-icon>
            <span>题目数量：{{ testInfo.questionCount }} 题</span>
          </div>
          <div class="info-item">
            <el-icon><Clock /></el-icon>
            <span>预计时间：{{ testInfo.estimatedTime }} 分钟</span>
          </div>
        </div>

        <div class="test-instructions">
          <h3>测试说明</h3>
          <ul>
            <li>请根据您最近一周的实际感受进行选择</li>
            <li>每道题目只能选择一个答案</li>
            <li>请认真作答，不要遗漏题目</li>
            <li>测试结果仅供参考，如有需要请咨询专业人士</li>
          </ul>
        </div>

        <el-button type="primary" size="large" @click="startTest" class="start-button">
          开始测试
        </el-button>
      </div>
    </div>

    <div v-else class="test-content">
      <div class="test-header">
        <div class="progress-info">
          <span class="current-question">第 {{ currentQuestionIndex + 1 }} 题</span>
          <span class="total-questions">/ 共 {{ questions.length }} 题</span>
        </div>
        <div class="timer">
          <el-icon><Clock /></el-icon>
          <span>{{ formatTime(elapsedTime) }}</span>
        </div>
      </div>

      <el-progress 
        :percentage="progress" 
        :show-text="false"
        class="progress-bar"
      />

      <div class="question-card" v-if="currentQuestion">
        <h2 class="question-text">
          {{ currentQuestion.questionNo }}. {{ currentQuestion.questionText }}
        </h2>

        <div class="options-list">
          <div
            v-for="option in currentQuestion.options"
            :key="option.key"
            class="option-item"
            :class="{ selected: answers[currentQuestionIndex] === option.key }"
            @click="selectOption(option.key, option.score)"
          >
            <div class="option-radio">
              <span v-if="answers[currentQuestionIndex] === option.key">✓</span>
            </div>
            <div class="option-content">
              <span class="option-label">{{ option.key }}</span>
              <span class="option-text">{{ option.value }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="navigation-buttons">
        <el-button 
          @click="previousQuestion" 
          :disabled="currentQuestionIndex === 0"
        >
          上一题
        </el-button>
        <el-button 
          v-if="currentQuestionIndex < questions.length - 1"
          type="primary" 
          @click="nextQuestion"
          :disabled="!answers[currentQuestionIndex]"
        >
          下一题
        </el-button>
        <el-button 
          v-else
          type="success" 
          @click="submitTest"
          :disabled="!isAllAnswered"
          :loading="submitting"
        >
          提交测试
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Document, Clock } from '@element-plus/icons-vue'
import { getTestList, getTestQuestions, submitTest as submitTestApi } from '@/api/test'

const route = useRoute()
const router = useRouter()

const testId = ref(route.params.id)
const testInfo = ref({})
const questions = ref([])
const answers = ref([])
const scores = ref([])
const currentQuestionIndex = ref(0)
const testStarted = ref(false)
const elapsedTime = ref(0)
const submitting = ref(false)
let timer = null

const currentQuestion = computed(() => questions.value[currentQuestionIndex.value])
const progress = computed(() => ((currentQuestionIndex.value + 1) / questions.value.length) * 100)
const isAllAnswered = computed(() => answers.value.every(answer => answer !== null))

onMounted(async () => {
  await loadTestInfo()
  await loadQuestions()
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
})

const loadTestInfo = async () => {
  try {
    const res = await getTestList()
    testInfo.value = res.data.find(t => t.id == testId.value) || {}
  } catch (error) {
    ElMessage.error('加载测试信息失败')
  }
}

const loadQuestions = async () => {
  try {
    const res = await getTestQuestions(testId.value)
    questions.value = res.data
    answers.value = new Array(questions.value.length).fill(null)
    scores.value = new Array(questions.value.length).fill(0)
  } catch (error) {
    ElMessage.error('加载题目失败')
  }
}

const startTest = () => {
  testStarted.value = true
  timer = setInterval(() => {
    elapsedTime.value++
  }, 1000)
}

const selectOption = (value, score) => {
  answers.value[currentQuestionIndex.value] = value
  scores.value[currentQuestionIndex.value] = score
}

const nextQuestion = () => {
  if (currentQuestionIndex.value < questions.value.length - 1) {
    currentQuestionIndex.value++
  }
}

const previousQuestion = () => {
  if (currentQuestionIndex.value > 0) {
    currentQuestionIndex.value--
  }
}

const submitTest = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要提交测试吗？提交后将无法修改答案。',
      '提交确认',
      {
        confirmButtonText: '确定提交',
        cancelButtonText: '再检查一下',
        type: 'warning'
      }
    )

    submitting.value = true
    
    const answerData = questions.value.map((q, index) => ({
      questionId: q.id,
      answer: answers.value[index],
      score: scores.value[index]
    }))

    const res = await submitTestApi({
      testId: testId.value,
      answers: answerData,
      testDuration: elapsedTime.value
    })

    clearInterval(timer)
    ElMessage.success('测试提交成功')
    
    // 跳转到结果页面
    router.push(`/student/tests/result/${res.data.id}`)
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('提交失败，请重试')
    }
  } finally {
    submitting.value = false
  }
}

const formatTime = (seconds) => {
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}
</script>

<style scoped lang="scss">
@import '@/assets/styles/variables.scss';

.test-detail-page {
  max-width: 900px;
  margin: 0 auto;
}

.test-intro {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 60vh;
}

.intro-card {
  background: white;
  border-radius: $radius-lg;
  padding: $spacing-xl * 2;
  box-shadow: $shadow-xl;
  max-width: 600px;
  text-align: center;
}

.test-title {
  font-size: 32px;
  font-weight: 700;
  color: $text-primary;
  margin-bottom: 16px;
}

.test-description {
  font-size: 16px;
  color: $text-secondary;
  line-height: 1.6;
  margin-bottom: 32px;
}

.test-info {
  display: flex;
  justify-content: center;
  gap: 32px;
  margin-bottom: 32px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  color: $text-secondary;

  .el-icon {
    color: $primary-color;
    font-size: 20px;
  }
}

.test-instructions {
  text-align: left;
  background: $bg-color;
  padding: 24px;
  border-radius: $radius-md;
  margin-bottom: 32px;

  h3 {
    font-size: 18px;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: 16px;
  }

  ul {
    list-style: none;
    padding: 0;
    margin: 0;

    li {
      padding: 8px 0;
      padding-left: 24px;
      position: relative;
      color: $text-secondary;
      line-height: 1.6;

      &::before {
        content: '•';
        position: absolute;
        left: 8px;
        color: $primary-color;
        font-weight: bold;
      }
    }
  }
}

.start-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
}

.test-content {
  background: white;
  border-radius: $radius-lg;
  padding: $spacing-xl;
  box-shadow: $shadow-md;
}

.test-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.progress-info {
  font-size: 16px;
  color: $text-secondary;

  .current-question {
    font-size: 24px;
    font-weight: 700;
    color: $primary-color;
  }
}

.timer {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
  color: $text-secondary;

  .el-icon {
    color: $primary-color;
  }
}

.progress-bar {
  margin-bottom: 32px;
}

.question-card {
  margin-bottom: 32px;
}

.question-text {
  font-size: 20px;
  font-weight: 600;
  color: $text-primary;
  line-height: 1.6;
  margin-bottom: 32px;
}

.options-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.option-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  border: 2px solid $border-color;
  border-radius: $radius-md;
  cursor: pointer;
  transition: $transition-base;

  &:hover {
    border-color: $primary-color;
    background: rgba(99, 102, 241, 0.05);
  }

  &.selected {
    border-color: $primary-color;
    background: rgba(99, 102, 241, 0.1);

    .option-radio {
      background: $primary-color;
      color: white;
    }
  }
}

.option-radio {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: 2px solid $border-color;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  flex-shrink: 0;
  transition: $transition-base;
}

.option-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.option-label {
  font-size: 16px;
  font-weight: 600;
  color: $text-primary;
}

.option-text {
  font-size: 14px;
  color: $text-secondary;
}

.navigation-buttons {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  margin-top: 32px;

  .el-button {
    flex: 1;
    height: 44px;
  }
}

@media (max-width: 768px) {
  .intro-card {
    padding: $spacing-xl;
  }

  .test-content {
    padding: $spacing-lg;
  }

  .question-text {
    font-size: 18px;
  }
}
</style>

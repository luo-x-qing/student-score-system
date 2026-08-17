<template>
  <div class="score-entry">
    <div class="header">
      <h2>成绩录入</h2>
    </div>

    <!-- 筛选条件 -->
    <div class="filter-box">
      <div class="filter-item">
        <label>班级：</label>
        <input v-model="filters.className" placeholder="输入班级名称" @keyup.enter="loadStudents" />
      </div>
      <div class="filter-item">
        <label>学科：</label>
        <select v-model="filters.subjectId">
          <option value="">请选择学科</option>
          <option v-for="subject in subjects" :key="subject.id" :value="subject.id">
            {{ subject.subjectName }}
          </option>
        </select>
      </div>
      <div class="filter-item">
        <label>考试类型：</label>
        <select v-model="filters.examTypeId">
          <option value="">请选择考试类型</option>
          <option v-for="examType in examTypes" :key="examType.id" :value="examType.id">
            {{ examType.typeName }}
          </option>
        </select>
      </div>
      <div class="filter-actions">
        <button @click="loadScores" class="btn-primary" :disabled="!canLoad">加载成绩</button>
        <button @click="handleBatchSave" class="btn-success" :disabled="!scores.length || saving">
          {{ saving ? '保存中...' : '批量保存' }}
        </button>
      </div>
    </div>

    <!-- 提示信息 -->
    <div v-if="!canLoad" class="hint-box warning">
      请选择班级、学科和考试类型后再加载成绩
    </div>

    <!-- 成绩录入表格 -->
    <div v-if="scores.length > 0" class="table-container">
      <table class="score-table">
        <thead>
          <tr>
            <th class="sticky-col">学号</th>
            <th class="sticky-col">姓名</th>
            <th>成绩</th>
            <th>状态</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(item, index) in scores" :key="item.student.id">
            <td class="sticky-col">{{ item.student.studentNo }}</td>
            <td class="sticky-col">{{ item.student.name }}</td>
            <td class="score-input-cell">
              <input
                v-model="item.score"
                type="number"
                step="0.5"
                min="0"
                max="100"
                placeholder="未录入"
                class="score-input"
                :class="{ 'has-error': item.error }"
                @input="validateScore(item)"
              />
              <span v-if="item.error" class="error-hint">{{ item.error }}</span>
            </td>
            <td>
              <span v-if="item.saved" class="status-saved">✓ 已保存</span>
              <span v-else-if="item.score !== null && item.score !== ''" class="status-modified">待保存</span>
              <span v-else class="status-empty">-</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 操作提示 -->
    <div v-if="scores.length > 0" class="hint-box">
      <p><strong>操作说明：</strong></p>
      <ul>
        <li>成绩范围：0-100，支持小数（如：85.5）</li>
        <li>留空表示缺考或未录入</li>
        <li>输入 0 表示得 0 分（与留空不同）</li>
        <li>点击「批量保存」一次性保存所有成绩</li>
        <li>保存失败时会显示具体错误信息</li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'

/**
 * 数据定义
 */
const subjects = ref([])
const examTypes = ref([])
const students = ref([])
const scores = ref([])
const saving = ref(false)

const filters = reactive({
  className: '',
  subjectId: '',
  examTypeId: ''
})

/**
 * 计算属性：是否可以加载成绩
 */
const canLoad = computed(() => {
  return filters.className.trim() && filters.subjectId && filters.examTypeId
})

/**
 * 加载学科列表
 */
const loadSubjects = async () => {
  try {
    const response = await fetch('/api/subjects')
    const result = await response.json()
    if (result.code === 200 && result.data) {
      subjects.value = result.data
    }
  } catch (error) {
    console.error('加载学科列表失败：', error)
  }
}

/**
 * 加载考试类型列表
 */
const loadExamTypes = async () => {
  try {
    const response = await fetch('/api/exam-types')
    const result = await response.json()
    if (result.code === 200 && result.data) {
      examTypes.value = result.data
    }
  } catch (error) {
    console.error('加载考试类型列表失败：', error)
  }
}

/**
 * 加载学生列表（根据班级筛选）
 */
const loadStudents = async () => {
  if (!filters.className.trim()) {
    return
  }

  try {
    const params = new URLSearchParams({
      className: filters.className,
      page: 1,
      pageSize: 100
    })

    const response = await fetch(`/api/students?${params}`)
    const result = await response.json()

    if (result.code === 200 && result.data) {
      students.value = result.data.records || []
    } else {
      alert('加载学生列表失败：' + result.message)
    }
  } catch (error) {
    alert('网络错误：' + error.message)
  }
}

/**
 * 加载成绩
 */
const loadScores = async () => {
  if (!canLoad.value) {
    alert('请选择班级、学科和考试类型')
    return
  }

  // 先加载学生列表
  await loadStudents()

  if (students.value.length === 0) {
    alert('该班级没有学生')
    return
  }

  // 加载该学科和考试类型的所有成绩
  try {
    const response = await fetch(`/api/scores/subject/${filters.subjectId}`)
    const result = await response.json()

    if (result.code === 200 && result.data) {
      const existingScores = result.data.filter(
        score => score.examTypeId === parseInt(filters.examTypeId)
      )

      // 构建成绩录入矩阵
      scores.value = students.value.map(student => {
        const existingScore = existingScores.find(
          score => score.studentId === student.id
        )

        return {
          student: student,
          score: existingScore ? existingScore.score : '',
          scoreId: existingScore ? existingScore.id : null,
          saved: !!existingScore,
          error: null
        }
      })
    }
  } catch (error) {
    alert('加载成绩失败：' + error.message)
  }
}

/**
 * 校验成绩
 */
const validateScore = (item) => {
  item.error = null

  if (item.score === null || item.score === '') {
    // 空值表示缺考，允许
    return true
  }

  const score = parseFloat(item.score)
  if (isNaN(score)) {
    item.error = '请输入有效数字'
    return false
  }

  if (score < 0 || score > 100) {
    item.error = '成绩范围：0-100'
    return false
  }

  return true
}

/**
 * 批量保存成绩
 */
const handleBatchSave = async () => {
  // 校验所有成绩
  let hasError = false
  scores.value.forEach(item => {
    if (!validateScore(item)) {
      hasError = true
    }
  })

  if (hasError) {
    alert('存在无效成绩，请检查标红的单元格')
    return
  }

  // 构建批量保存请求
  const scoreRequests = scores.value
    .filter(item => item.score !== null && item.score !== '')
    .map(item => ({
      studentId: item.student.id,
      subjectId: parseInt(filters.subjectId),
      examTypeId: parseInt(filters.examTypeId),
      score: parseFloat(item.score)
    }))

  if (scoreRequests.length === 0) {
    alert('没有需要保存的成绩')
    return
  }

  saving.value = true
  try {
    const response = await fetch('/api/scores/batch', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ scores: scoreRequests })
    })

    const result = await response.json()

    if (result.code === 200) {
      alert(`批量保存成功：${scoreRequests.length} 条成绩`)
      // 重新加载成绩
      loadScores()
    } else {
      alert('批量保存失败：' + result.message)
    }
  } catch (error) {
    alert('网络错误：' + error.message)
  } finally {
    saving.value = false
  }
}

/**
 * 页面加载时初始化
 */
onMounted(() => {
  loadSubjects()
  loadExamTypes()
})
</script>

<style scoped>
.score-entry {
  max-width: 1400px;
  margin: 0 auto;
}

.header {
  margin-bottom: 20px;
}

.header h2 {
  margin: 0;
  color: #333;
}

/* 筛选框样式 */
.filter-box {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
  padding: 15px;
  background: white;
  border-radius: 4px;
  flex-wrap: wrap;
  align-items: center;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-item label {
  font-size: 14px;
  color: #606266;
  white-space: nowrap;
}

.filter-item input,
.filter-item select {
  width: 180px;
  padding: 8px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 14px;
}

.filter-actions {
  display: flex;
  gap: 10px;
  margin-left: auto;
}

/* 提示框样式 */
.hint-box {
  padding: 15px;
  margin-bottom: 20px;
  background: #f0f9ff;
  border: 1px solid #409eff;
  border-radius: 4px;
  color: #409eff;
  font-size: 14px;
}

.hint-box.warning {
  background: #fdf6ec;
  border-color: #e6a23c;
  color: #e6a23c;
}

.hint-box ul {
  margin: 10px 0 0 20px;
  padding: 0;
}

.hint-box li {
  margin: 5px 0;
}

/* 表格样式 */
.table-container {
  background: white;
  border-radius: 4px;
  overflow-x: auto;
  margin-bottom: 20px;
}

.score-table {
  width: 100%;
  border-collapse: collapse;
}

.score-table th,
.score-table td {
  padding: 12px 15px;
  text-align: left;
  border-bottom: 1px solid #ebeef5;
}

.score-table th {
  background-color: #f5f7fa;
  color: #909399;
  font-weight: 500;
  font-size: 14px;
}

.score-table td {
  color: #606266;
  font-size: 14px;
}

.score-table tbody tr:hover {
  background-color: #f5f7fa;
}

.sticky-col {
  position: sticky;
  left: 0;
  background: white;
  z-index: 1;
}

.score-table thead .sticky-col {
  background-color: #f5f7fa;
  z-index: 2;
}

.score-input-cell {
  position: relative;
}

.score-input {
  width: 100px;
  padding: 6px 10px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 14px;
}

.score-input:focus {
  outline: none;
  border-color: #409eff;
}

.score-input.has-error {
  border-color: #f56c6c;
  background-color: #fef0f0;
}

.error-hint {
  display: block;
  margin-top: 4px;
  font-size: 12px;
  color: #f56c6c;
}

.status-saved {
  color: #67c23a;
  font-size: 14px;
}

.status-modified {
  color: #e6a23c;
  font-size: 14px;
}

.status-empty {
  color: #c0c4cc;
  font-size: 14px;
}

/* 按钮样式 */
.btn-primary {
  padding: 10px 20px;
  background-color: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-primary:hover:not(:disabled) {
  background-color: #66b1ff;
}

.btn-primary:disabled {
  background-color: #a0cfff;
  cursor: not-allowed;
}

.btn-success {
  padding: 10px 20px;
  background-color: #67c23a;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-success:hover:not(:disabled) {
  background-color: #85ce61;
}

.btn-success:disabled {
  background-color: #b3e19d;
  cursor: not-allowed;
}
</style>

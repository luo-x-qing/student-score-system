<template>
  <div class="ranking">
    <div class="header">
      <h2>成绩排名</h2>
    </div>

    <!-- 筛选条件 -->
    <div class="filter-box">
      <div class="filter-item">
        <label>排名维度：</label>
        <select v-model="filters.rankingType">
          <option value="subject">按学科排名</option>
          <option value="comprehensive">按综合得分排名</option>
        </select>
      </div>
      <div class="filter-item" v-if="filters.rankingType === 'subject'">
        <label>学科：</label>
        <select v-model="filters.subjectId">
          <option value="">请选择学科</option>
          <option v-for="subject in subjects" :key="subject.id" :value="subject.id">
            {{ subject.subjectName }}
          </option>
        </select>
      </div>
      <div class="filter-item">
        <label>班级：</label>
        <input v-model="filters.className" placeholder="输入班级（留空查全部）" />
      </div>
      <div class="filter-item">
        <label>排序：</label>
        <select v-model="filters.ascending">
          <option :value="false">降序（高分在前）</option>
          <option :value="true">升序（低分在前）</option>
        </select>
      </div>
      <div class="filter-actions">
        <button @click="loadRanking" class="btn-primary" :disabled="!canLoad || loading">
          {{ loading ? '加载中...' : '查询排名' }}
        </button>
      </div>
    </div>

    <!-- 提示信息 -->
    <div v-if="!canLoad" class="hint-box warning">
      {{ filters.rankingType === 'subject' ? '请选择学科后再查询排名' : '请点击「查询排名」按钮' }}
    </div>

    <!-- 排名统计信息 -->
    <div v-if="rankings.length > 0" class="stats-box">
      <div class="stat-item">
        <span class="stat-label">总人数：</span>
        <span class="stat-value">{{ rankings.length }}</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">已完成：</span>
        <span class="stat-value success">{{ completedCount }}</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">未完成：</span>
        <span class="stat-value warning">{{ incompleteCount }}</span>
      </div>
    </div>

    <!-- 排名表格 -->
    <div v-if="rankings.length > 0" class="table-container">
      <table class="ranking-table">
        <thead>
          <tr>
            <th>排名</th>
            <th>学号</th>
            <th>姓名</th>
            <th>班级</th>
            <th>成绩</th>
            <th>状态</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(item, index) in rankings" :key="index" :class="{ 'incomplete-row': !item.complete }">
            <td class="rank-cell">
              <span v-if="item.rank" class="rank-badge" :class="getRankClass(item.rank)">
                {{ item.rank }}
              </span>
              <span v-else class="rank-badge incomplete">-</span>
            </td>
            <td>{{ item.studentNo }}</td>
            <td>{{ item.studentName }}</td>
            <td>{{ item.className }}</td>
            <td class="score-cell">
              <span v-if="item.score !== null">{{ item.score }}</span>
              <span v-else class="no-score">未完成</span>
            </td>
            <td>
              <span v-if="item.complete" class="status-complete">✓ 完成</span>
              <span v-else class="status-incomplete">未完成</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 空状态 -->
    <div v-if="!loading && rankings.length === 0 && hasQueried" class="empty-state">
      <p>暂无排名数据</p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'

/**
 * 数据定义
 */
const subjects = ref([])
const rankings = ref([])
const loading = ref(false)
const hasQueried = ref(false)

const filters = reactive({
  rankingType: 'subject',
  subjectId: '',
  className: '',
  ascending: false
})

/**
 * 计算属性：是否可以加载
 */
const canLoad = computed(() => {
  if (filters.rankingType === 'subject') {
    return !!filters.subjectId
  }
  return true
})

/**
 * 计算属性：已完成人数
 */
const completedCount = computed(() => {
  return rankings.value.filter(r => r.complete).length
})

/**
 * 计算属性：未完成人数
 */
const incompleteCount = computed(() => {
  return rankings.value.filter(r => !r.complete).length
})

/**
 * 获取排名样式类
 */
const getRankClass = (rank) => {
  if (rank === 1) return 'rank-1'
  if (rank === 2) return 'rank-2'
  if (rank === 3) return 'rank-3'
  return ''
}

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
 * 加载排名
 */
const loadRanking = async () => {
  if (!canLoad.value) {
    return
  }

  loading.value = true
  hasQueried.value = true

  try {
    let url = ''
    const params = new URLSearchParams()

    if (filters.className) {
      params.append('className', filters.className)
    }
    params.append('ascending', filters.ascending)

    if (filters.rankingType === 'subject') {
      url = `/api/ranking/subject/${filters.subjectId}?${params}`
    } else {
      url = `/api/ranking/comprehensive?${params}`
    }

    const response = await fetch(url)
    const result = await response.json()

    if (result.code === 200 && result.data) {
      rankings.value = result.data
    } else {
      alert('查询排名失败：' + result.message)
      rankings.value = []
    }
  } catch (error) {
    alert('网络错误：' + error.message)
    rankings.value = []
  } finally {
    loading.value = false
  }
}

/**
 * 页面加载时初始化
 */
onMounted(() => {
  loadSubjects()
})
</script>

<style scoped>
.ranking {
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

/* 统计信息 */
.stats-box {
  display: flex;
  gap: 30px;
  padding: 15px;
  margin-bottom: 20px;
  background: white;
  border-radius: 4px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.stat-label {
  font-size: 14px;
  color: #606266;
}

.stat-value {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.stat-value.success {
  color: #67c23a;
}

.stat-value.warning {
  color: #e6a23c;
}

/* 表格样式 */
.table-container {
  background: white;
  border-radius: 4px;
  overflow-x: auto;
  margin-bottom: 20px;
}

.ranking-table {
  width: 100%;
  border-collapse: collapse;
}

.ranking-table th,
.ranking-table td {
  padding: 12px 15px;
  text-align: left;
  border-bottom: 1px solid #ebeef5;
}

.ranking-table th {
  background-color: #f5f7fa;
  color: #909399;
  font-weight: 500;
  font-size: 14px;
}

.ranking-table td {
  color: #606266;
  font-size: 14px;
}

.ranking-table tbody tr:hover {
  background-color: #f5f7fa;
}

.ranking-table tbody tr.incomplete-row {
  background-color: #fafafa;
}

/* 排名徽章 */
.rank-cell {
  text-align: center;
}

.rank-badge {
  display: inline-block;
  min-width: 36px;
  padding: 4px 8px;
  border-radius: 4px;
  font-weight: bold;
  font-size: 16px;
  text-align: center;
}

.rank-badge.rank-1 {
  background: linear-gradient(135deg, #ffd700 0%, #ffed4e 100%);
  color: #8b6914;
}

.rank-badge.rank-2 {
  background: linear-gradient(135deg, #c0c0c0 0%, #e8e8e8 100%);
  color: #5a5a5a;
}

.rank-badge.rank-3 {
  background: linear-gradient(135deg, #cd7f32 0%, #e6a65c 100%);
  color: #5c3d1f;
}

.rank-badge.incomplete {
  background: #f5f7fa;
  color: #c0c4cc;
}

/* 成绩单元格 */
.score-cell {
  font-weight: 500;
  font-size: 15px;
}

.no-score {
  color: #c0c4cc;
}

/* 状态 */
.status-complete {
  color: #67c23a;
  font-size: 14px;
}

.status-incomplete {
  color: #e6a23c;
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

/* 空状态 */
.empty-state {
  padding: 60px 20px;
  text-align: center;
  background: white;
  border-radius: 4px;
  color: #909399;
  font-size: 16px;
}
</style>

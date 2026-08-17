<template>
  <div class="exam-type-management">
    <div class="header">
      <h2>考试类型配置</h2>
      <button @click="showCreateDialog" class="btn-primary">新增考试类型</button>
    </div>

    <!-- 考试类型列表表格 -->
    <div class="table-container">
      <table class="exam-type-table">
        <thead>
          <tr>
            <th>考试类型名称</th>
            <th>比率（%）</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="4" class="loading-cell">加载中...</td>
          </tr>
          <tr v-else-if="examTypes.length === 0">
            <td colspan="4" class="empty-cell">暂无数据</td>
          </tr>
          <tr v-else v-for="examType in examTypes" :key="examType.id">
            <td>{{ examType.typeName }}</td>
            <td>{{ examType.rate }}%</td>
            <td>{{ formatDateTime(examType.createdAt) }}</td>
            <td class="action-cell">
              <button @click="showEditDialog(examType)" class="btn-text">编辑</button>
              <button @click="handleDelete(examType)" class="btn-text btn-danger">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 总比率提示 -->
    <div v-if="examTypes.length > 0" class="total-rate-hint">
      <strong>当前总比率：{{ totalRate }}%</strong>
      <span v-if="totalRate !== 100" class="warning">
        （建议总比率为 100%，用于计算学科总评）
      </span>
      <span v-else class="success">✓ 总比率正确</span>
    </div>

    <!-- 新增/编辑对话框 -->
    <div v-if="dialogVisible" class="dialog-overlay" @click.self="closeDialog">
      <div class="dialog">
        <div class="dialog-header">
          <h3>{{ isEdit ? '编辑考试类型' : '新增考试类型' }}</h3>
          <button @click="closeDialog" class="close-btn">&times;</button>
        </div>
        <div class="dialog-body">
          <div class="form-item">
            <label class="required">考试类型名称：</label>
            <input v-model="form.typeName" placeholder="请输入考试类型名称（如：期中考试、期末考试）" />
          </div>
          <div class="form-item">
            <label class="required">比率（%）：</label>
            <input
              v-model="form.rate"
              type="number"
              step="1"
              min="0"
              max="100"
              placeholder="请输入比率（0-100）"
            />
            <span class="hint">
              比率用于计算学科总评，例如：期中考试30%、期末考试50%、平时成绩20%
            </span>
          </div>
        </div>
        <div class="dialog-footer">
          <button @click="handleSubmit" class="btn-primary" :disabled="submitting">
            {{ submitting ? '提交中...' : '确定' }}
          </button>
          <button @click="closeDialog" class="btn-default">取消</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'

/**
 * 数据定义
 */
const loading = ref(false)
const examTypes = ref([])

const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const form = reactive({
  id: null,
  typeName: '',
  rate: ''
})

/**
 * 计算总比率
 */
const totalRate = computed(() => {
  return examTypes.value.reduce((sum, examType) => {
    return sum + parseFloat(examType.rate || 0)
  }, 0)
})

/**
 * 加载考试类型列表
 */
const loadExamTypes = async () => {
  loading.value = true
  try {
    const response = await fetch('/api/exam-types')
    const result = await response.json()

    if (result.code === 200 && result.data) {
      examTypes.value = result.data
    } else {
      alert('加载考试类型列表失败：' + result.message)
    }
  } catch (error) {
    alert('网络错误：' + error.message)
  } finally {
    loading.value = false
  }
}

/**
 * 格式化日期时间
 */
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  const date = new Date(dateTime)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

/**
 * 显示新增对话框
 */
const showCreateDialog = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

/**
 * 显示编辑对话框
 */
const showEditDialog = (examType) => {
  isEdit.value = true
  form.id = examType.id
  form.typeName = examType.typeName
  form.rate = examType.rate
  dialogVisible.value = true
}

/**
 * 关闭对话框
 */
const closeDialog = () => {
  dialogVisible.value = false
  resetForm()
}

/**
 * 重置表单
 */
const resetForm = () => {
  form.id = null
  form.typeName = ''
  form.rate = ''
}

/**
 * 提交表单（新增或编辑）
 */
const handleSubmit = async () => {
  // 表单校验
  if (!form.typeName.trim()) {
    alert('请输入考试类型名称')
    return
  }

  const rate = parseFloat(form.rate)
  if (isNaN(rate) || rate < 0 || rate > 100) {
    alert('请输入有效的比率（0-100）')
    return
  }

  submitting.value = true
  try {
    const url = isEdit.value ? `/api/exam-types/${form.id}` : '/api/exam-types'
    const method = isEdit.value ? 'PUT' : 'POST'
    const body = {
      typeName: form.typeName,
      rate: rate
    }

    const response = await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
    })

    const result = await response.json()

    if (result.code === 200) {
      alert(isEdit.value ? '编辑成功' : '新增成功')
      closeDialog()
      loadExamTypes()
    } else {
      alert('操作失败：' + result.message)
    }
  } catch (error) {
    alert('网络错误：' + error.message)
  } finally {
    submitting.value = false
  }
}

/**
 * 删除考试类型
 */
const handleDelete = async (examType) => {
  if (!confirm(`确定要删除考试类型「${examType.typeName}」吗？\n\n注意：如果该考试类型已被成绩引用，将无法删除。`)) {
    return
  }

  try {
    const response = await fetch(`/api/exam-types/${examType.id}`, {
      method: 'DELETE'
    })

    const result = await response.json()

    if (result.code === 200) {
      alert('删除成功')
      loadExamTypes()
    } else if (result.code === 409) {
      // 引用冲突
      alert('删除失败：该考试类型已被成绩引用，无法删除')
    } else {
      alert('删除失败：' + result.message)
    }
  } catch (error) {
    alert('网络错误：' + error.message)
  }
}

/**
 * 页面加载时获取考试类型列表
 */
onMounted(() => {
  loadExamTypes()
})
</script>

<style scoped>
.exam-type-management {
  max-width: 1200px;
  margin: 0 auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header h2 {
  margin: 0;
  color: #333;
}

/* 表格样式 */
.table-container {
  background: white;
  border-radius: 4px;
  overflow-x: auto;
  margin-bottom: 15px;
}

.exam-type-table {
  width: 100%;
  border-collapse: collapse;
}

.exam-type-table th,
.exam-type-table td {
  padding: 12px 15px;
  text-align: left;
  border-bottom: 1px solid #ebeef5;
}

.exam-type-table th {
  background-color: #f5f7fa;
  color: #909399;
  font-weight: 500;
  font-size: 14px;
}

.exam-type-table td {
  color: #606266;
  font-size: 14px;
}

.exam-type-table tbody tr:hover {
  background-color: #f5f7fa;
}

.loading-cell,
.empty-cell {
  text-align: center;
  color: #909399;
  padding: 40px;
}

.action-cell {
  display: flex;
  gap: 10px;
}

/* 总比率提示 */
.total-rate-hint {
  background: white;
  padding: 15px;
  border-radius: 4px;
  margin-bottom: 20px;
  font-size: 14px;
  color: #606266;
}

.total-rate-hint .warning {
  color: #e6a23c;
  margin-left: 10px;
}

.total-rate-hint .success {
  color: #67c23a;
  margin-left: 10px;
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

.btn-default {
  padding: 10px 20px;
  background-color: white;
  color: #606266;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-default:hover {
  color: #409eff;
  border-color: #c6e2ff;
  background-color: #ecf5ff;
}

.btn-text {
  padding: 0;
  background: none;
  border: none;
  color: #409eff;
  font-size: 14px;
  cursor: pointer;
  text-decoration: none;
}

.btn-text:hover {
  color: #66b1ff;
}

.btn-danger {
  color: #f56c6c;
}

.btn-danger:hover {
  color: #f78989;
}

/* 对话框样式 */
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.dialog {
  background: white;
  border-radius: 4px;
  width: 500px;
  max-width: 90%;
  max-height: 90vh;
  overflow: auto;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #ebeef5;
}

.dialog-header h3 {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

.close-btn {
  background: none;
  border: none;
  font-size: 28px;
  color: #909399;
  cursor: pointer;
  line-height: 1;
  padding: 0;
  width: 28px;
  height: 28px;
}

.close-btn:hover {
  color: #409eff;
}

.dialog-body {
  padding: 20px;
}

.form-item {
  margin-bottom: 20px;
}

.form-item label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  color: #606266;
}

.form-item label.required::before {
  content: '* ';
  color: #f56c6c;
}

.form-item input {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 14px;
  box-sizing: border-box;
}

.form-item input:focus {
  outline: none;
  border-color: #409eff;
}

.form-item .hint {
  display: block;
  margin-top: 5px;
  font-size: 12px;
  color: #909399;
}

.dialog-footer {
  padding: 20px;
  border-top: 1px solid #ebeef5;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>

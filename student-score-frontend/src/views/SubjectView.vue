<template>
  <div class="subject-management">
    <div class="header">
      <h2>学科配置</h2>
      <button @click="showCreateDialog" class="btn-primary">新增学科</button>
    </div>

    <!-- 学科列表表格 -->
    <div class="table-container">
      <table class="subject-table">
        <thead>
          <tr>
            <th>学科名称</th>
            <th>权重系数</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="4" class="loading-cell">加载中...</td>
          </tr>
          <tr v-else-if="subjects.length === 0">
            <td colspan="4" class="empty-cell">暂无数据</td>
          </tr>
          <tr v-else v-for="subject in subjects" :key="subject.id">
            <td>{{ subject.subjectName }}</td>
            <td>{{ subject.weightRate }}</td>
            <td>{{ formatDateTime(subject.createdAt) }}</td>
            <td class="action-cell">
              <button @click="showEditDialog(subject)" class="btn-text">编辑</button>
              <button @click="handleDelete(subject)" class="btn-text btn-danger">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 新增/编辑对话框 -->
    <div v-if="dialogVisible" class="dialog-overlay" @click.self="closeDialog">
      <div class="dialog">
        <div class="dialog-header">
          <h3>{{ isEdit ? '编辑学科' : '新增学科' }}</h3>
          <button @click="closeDialog" class="close-btn">&times;</button>
        </div>
        <div class="dialog-body">
          <div class="form-item">
            <label class="required">学科名称：</label>
            <input v-model="form.subjectName" placeholder="请输入学科名称（如：语文、数学）" />
          </div>
          <div class="form-item">
            <label class="required">权重系数：</label>
            <input
              v-model="form.weightRate"
              type="number"
              step="0.1"
              min="0.1"
              placeholder="请输入权重系数（必须为正数）"
            />
            <span class="hint">权重用于计算综合得分，主科可设置1.5，副科可设置1.0</span>
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
import { ref, reactive, onMounted } from 'vue'

/**
 * 数据定义
 */
const loading = ref(false)
const subjects = ref([])

const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const form = reactive({
  id: null,
  subjectName: '',
  weightRate: ''
})

/**
 * 加载学科列表
 */
const loadSubjects = async () => {
  loading.value = true
  try {
    const response = await fetch('/api/subjects')
    const result = await response.json()

    if (result.code === 200 && result.data) {
      subjects.value = result.data
    } else {
      alert('加载学科列表失败：' + result.message)
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
const showEditDialog = (subject) => {
  isEdit.value = true
  form.id = subject.id
  form.subjectName = subject.subjectName
  form.weightRate = subject.weightRate
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
  form.subjectName = ''
  form.weightRate = ''
}

/**
 * 提交表单（新增或编辑）
 */
const handleSubmit = async () => {
  // 表单校验
  if (!form.subjectName.trim()) {
    alert('请输入学科名称')
    return
  }

  const weightRate = parseFloat(form.weightRate)
  if (!weightRate || weightRate <= 0) {
    alert('请输入有效的权重系数（必须为正数）')
    return
  }

  submitting.value = true
  try {
    const url = isEdit.value ? `/api/subjects/${form.id}` : '/api/subjects'
    const method = isEdit.value ? 'PUT' : 'POST'
    const body = {
      subjectName: form.subjectName,
      weightRate: weightRate
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
      loadSubjects()
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
 * 删除学科
 */
const handleDelete = async (subject) => {
  if (!confirm(`确定要删除学科「${subject.subjectName}」吗？\n\n注意：如果该学科已被成绩引用，将无法删除。`)) {
    return
  }

  try {
    const response = await fetch(`/api/subjects/${subject.id}`, {
      method: 'DELETE'
    })

    const result = await response.json()

    if (result.code === 200) {
      alert('删除成功')
      loadSubjects()
    } else if (result.code === 409) {
      // 引用冲突
      alert('删除失败：该学科已被成绩引用，无法删除')
    } else {
      alert('删除失败：' + result.message)
    }
  } catch (error) {
    alert('网络错误：' + error.message)
  }
}

/**
 * 页面加载时获取学科列表
 */
onMounted(() => {
  loadSubjects()
})
</script>

<style scoped>
.subject-management {
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
}

.subject-table {
  width: 100%;
  border-collapse: collapse;
}

.subject-table th,
.subject-table td {
  padding: 12px 15px;
  text-align: left;
  border-bottom: 1px solid #ebeef5;
}

.subject-table th {
  background-color: #f5f7fa;
  color: #909399;
  font-weight: 500;
  font-size: 14px;
}

.subject-table td {
  color: #606266;
  font-size: 14px;
}

.subject-table tbody tr:hover {
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

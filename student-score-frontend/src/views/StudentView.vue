<template>
  <div class="student-management">
    <div class="header">
      <h2>学生管理</h2>
      <button @click="showCreateDialog" class="btn-primary">新增学生</button>
    </div>

    <!-- 搜索区域 -->
    <div class="search-box">
      <div class="search-item">
        <label>学号：</label>
        <input v-model="searchForm.studentNo" placeholder="输入学号搜索" @keyup.enter="handleSearch" />
      </div>
      <div class="search-item">
        <label>姓名：</label>
        <input v-model="searchForm.name" placeholder="输入姓名搜索" @keyup.enter="handleSearch" />
      </div>
      <div class="search-item">
        <label>班级：</label>
        <input v-model="searchForm.className" placeholder="输入班级搜索" @keyup.enter="handleSearch" />
      </div>
      <div class="search-actions">
        <button @click="handleSearch" class="btn-primary">搜索</button>
        <button @click="handleReset" class="btn-default">重置</button>
      </div>
    </div>

    <!-- 学生列表表格 -->
    <div class="table-container">
      <table class="student-table">
        <thead>
          <tr>
            <th>学号</th>
            <th>姓名</th>
            <th>性别</th>
            <th>班级</th>
            <th>备注</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="6" class="loading-cell">加载中...</td>
          </tr>
          <tr v-else-if="students.length === 0">
            <td colspan="6" class="empty-cell">暂无数据</td>
          </tr>
          <tr v-else v-for="student in students" :key="student.id">
            <td>{{ student.studentNo }}</td>
            <td>{{ student.name }}</td>
            <td>{{ student.gender || '-' }}</td>
            <td>{{ student.className }}</td>
            <td>{{ student.remarks || '-' }}</td>
            <td class="action-cell">
              <button @click="showEditDialog(student)" class="btn-text">编辑</button>
              <button @click="handleDelete(student)" class="btn-text btn-danger">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 分页器 -->
    <div class="pagination" v-if="pagination.total > 0">
      <div class="pagination-info">
        共 {{ pagination.total }} 条记录，每页
        <select v-model.number="pagination.pageSize" @change="handlePageSizeChange">
          <option :value="10">10</option>
          <option :value="20">20</option>
          <option :value="50">50</option>
          <option :value="100">100</option>
        </select>
        条
      </div>
      <div class="pagination-controls">
        <button @click="handlePageChange(1)" :disabled="pagination.page === 1">首页</button>
        <button @click="handlePageChange(pagination.page - 1)" :disabled="pagination.page === 1">上一页</button>
        <span class="page-number">第 {{ pagination.page }} / {{ pagination.totalPages }} 页</span>
        <button @click="handlePageChange(pagination.page + 1)" :disabled="pagination.page >= pagination.totalPages">下一页</button>
        <button @click="handlePageChange(pagination.totalPages)" :disabled="pagination.page >= pagination.totalPages">末页</button>
      </div>
    </div>

    <!-- 新增/编辑对话框 -->
    <div v-if="dialogVisible" class="dialog-overlay" @click.self="closeDialog">
      <div class="dialog">
        <div class="dialog-header">
          <h3>{{ isEdit ? '编辑学生' : '新增学生' }}</h3>
          <button @click="closeDialog" class="close-btn">&times;</button>
        </div>
        <div class="dialog-body">
          <div class="form-item">
            <label class="required">学号：</label>
            <input v-model="form.studentNo" placeholder="请输入学号" :disabled="isEdit" />
            <span v-if="isEdit" class="hint">学号不可修改</span>
          </div>
          <div class="form-item">
            <label class="required">姓名：</label>
            <input v-model="form.name" placeholder="请输入姓名" />
          </div>
          <div class="form-item">
            <label>性别：</label>
            <select v-model="form.gender">
              <option value="">请选择</option>
              <option value="男">男</option>
              <option value="女">女</option>
            </select>
          </div>
          <div class="form-item">
            <label class="required">班级：</label>
            <input v-model="form.className" placeholder="请输入班级" />
          </div>
          <div class="form-item">
            <label>备注：</label>
            <textarea v-model="form.remarks" placeholder="请输入备注" rows="3"></textarea>
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
const students = ref([])
const pagination = reactive({
  page: 1,
  pageSize: 20,
  total: 0,
  totalPages: 0
})

const searchForm = reactive({
  studentNo: '',
  name: '',
  className: ''
})

const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const form = reactive({
  id: null,
  studentNo: '',
  name: '',
  gender: '',
  className: '',
  remarks: ''
})

/**
 * 加载学生列表
 */
const loadStudents = async () => {
  loading.value = true
  try {
    const params = new URLSearchParams({
      page: pagination.page,
      pageSize: pagination.pageSize,
      ...(searchForm.studentNo && { studentNo: searchForm.studentNo }),
      ...(searchForm.name && { name: searchForm.name }),
      ...(searchForm.className && { className: searchForm.className })
    })

    const response = await fetch(`/api/students?${params}`)
    const result = await response.json()

    if (result.code === 200 && result.data) {
      students.value = result.data.records || []
      pagination.total = result.data.total || 0
      pagination.totalPages = result.data.totalPages || 0
    } else {
      alert('加载学生列表失败：' + result.message)
    }
  } catch (error) {
    alert('网络错误：' + error.message)
  } finally {
    loading.value = false
  }
}

/**
 * 搜索
 */
const handleSearch = () => {
  pagination.page = 1
  loadStudents()
}

/**
 * 重置搜索
 */
const handleReset = () => {
  searchForm.studentNo = ''
  searchForm.name = ''
  searchForm.className = ''
  pagination.page = 1
  loadStudents()
}

/**
 * 分页切换
 */
const handlePageChange = (page) => {
  pagination.page = page
  loadStudents()
}

/**
 * 每页条数切换
 */
const handlePageSizeChange = () => {
  pagination.page = 1
  loadStudents()
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
const showEditDialog = (student) => {
  isEdit.value = true
  form.id = student.id
  form.studentNo = student.studentNo
  form.name = student.name
  form.gender = student.gender || ''
  form.className = student.className
  form.remarks = student.remarks || ''
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
  form.studentNo = ''
  form.name = ''
  form.gender = ''
  form.className = ''
  form.remarks = ''
}

/**
 * 提交表单（新增或编辑）
 */
const handleSubmit = async () => {
  // 表单校验
  if (!form.studentNo.trim()) {
    alert('请输入学号')
    return
  }
  if (!form.name.trim()) {
    alert('请输入姓名')
    return
  }
  if (!form.className.trim()) {
    alert('请输入班级')
    return
  }

  submitting.value = true
  try {
    const url = isEdit.value ? `/api/students/${form.id}` : '/api/students'
    const method = isEdit.value ? 'PUT' : 'POST'
    const body = isEdit.value
      ? {
          name: form.name,
          gender: form.gender,
          className: form.className,
          remarks: form.remarks
        }
      : {
          studentNo: form.studentNo,
          name: form.name,
          gender: form.gender,
          className: form.className,
          remarks: form.remarks
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
      loadStudents()
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
 * 删除学生
 */
const handleDelete = async (student) => {
  if (!confirm(`确定要删除学生 ${student.name}（${student.studentNo}）吗？`)) {
    return
  }

  try {
    const response = await fetch(`/api/students/${student.id}`, {
      method: 'DELETE'
    })

    const result = await response.json()

    if (result.code === 200) {
      alert('删除成功')
      loadStudents()
    } else {
      alert('删除失败：' + result.message)
    }
  } catch (error) {
    alert('网络错误：' + error.message)
  }
}

/**
 * 页面加载时获取学生列表
 */
onMounted(() => {
  loadStudents()
})
</script>

<style scoped>
.student-management {
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

/* 搜索框样式 */
.search-box {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
  padding: 15px;
  background: white;
  border-radius: 4px;
  flex-wrap: wrap;
  align-items: center;
}

.search-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.search-item label {
  font-size: 14px;
  color: #606266;
  white-space: nowrap;
}

.search-item input {
  width: 180px;
  padding: 8px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 14px;
}

.search-actions {
  display: flex;
  gap: 10px;
}

/* 表格样式 */
.table-container {
  background: white;
  border-radius: 4px;
  overflow-x: auto;
  margin-bottom: 20px;
}

.student-table {
  width: 100%;
  border-collapse: collapse;
}

.student-table th,
.student-table td {
  padding: 12px 15px;
  text-align: left;
  border-bottom: 1px solid #ebeef5;
}

.student-table th {
  background-color: #f5f7fa;
  color: #909399;
  font-weight: 500;
  font-size: 14px;
}

.student-table td {
  color: #606266;
  font-size: 14px;
}

.student-table tbody tr:hover {
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

/* 分页器样式 */
.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background: white;
  border-radius: 4px;
}

.pagination-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #606266;
}

.pagination-info select {
  padding: 4px 8px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}

.pagination-controls {
  display: flex;
  gap: 8px;
  align-items: center;
}

.pagination-controls button {
  padding: 6px 12px;
  font-size: 14px;
  border: 1px solid #dcdfe6;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.pagination-controls button:hover:not(:disabled) {
  color: #409eff;
  border-color: #c6e2ff;
  background-color: #ecf5ff;
}

.pagination-controls button:disabled {
  color: #c0c4cc;
  cursor: not-allowed;
  background-color: #f5f7fa;
}

.page-number {
  font-size: 14px;
  color: #606266;
  margin: 0 8px;
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

.form-item input,
.form-item select,
.form-item textarea {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 14px;
  box-sizing: border-box;
}

.form-item input:disabled {
  background-color: #f5f7fa;
  color: #c0c4cc;
  cursor: not-allowed;
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

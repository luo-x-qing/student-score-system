<template>
  <div class="home">
    <h1>学生成绩处理系统</h1>
    <p>欢迎使用学生成绩处理系统</p>

    <!-- 后端健康检查 -->
    <div class="health-check">
      <h3>后端服务状态</h3>
      <button @click="checkBackendHealth" :disabled="checking">
        {{ checking ? '检查中...' : '检查后端连接' }}
      </button>
      <div v-if="healthStatus" class="status-box" :class="healthStatus.ok ? 'success' : 'error'">
        <p><strong>状态：</strong>{{ healthStatus.ok ? '✓ 连接成功' : '✗ 连接失败' }}</p>
        <p v-if="healthStatus.data"><strong>应用：</strong>{{ healthStatus.data.application }}</p>
        <p v-if="healthStatus.data"><strong>状态：</strong>{{ healthStatus.data.status }}</p>
        <p v-if="healthStatus.error"><strong>错误：</strong>{{ healthStatus.error }}</p>
      </div>
    </div>

    <div class="nav-links">
      <router-link to="/students">学生管理</router-link>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const checking = ref(false)
const healthStatus = ref(null)

/**
 * 检查后端健康状态
 * 调用后端 /api/health 接口验证连接
 */
const checkBackendHealth = async () => {
  checking.value = true
  healthStatus.value = null

  try {
    const response = await fetch('/api/health')
    const data = await response.json()

    if (response.ok) {
      healthStatus.value = {
        ok: true,
        data: data.data || data
      }
    } else {
      healthStatus.value = {
        ok: false,
        error: `HTTP ${response.status}: ${response.statusText}`
      }
    }
  } catch (error) {
    healthStatus.value = {
      ok: false,
      error: error.message || '无法连接到后端服务'
    }
  } finally {
    checking.value = false
  }
}
</script>

<style scoped>
.home {
  padding: 20px;
  text-align: center;
}

.health-check {
  margin: 30px auto;
  max-width: 500px;
}

.health-check h3 {
  margin-bottom: 15px;
  color: #333;
}

.health-check button {
  padding: 10px 20px;
  font-size: 14px;
  color: white;
  background-color: #409eff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.health-check button:hover:not(:disabled) {
  background-color: #66b1ff;
}

.health-check button:disabled {
  background-color: #a0cfff;
  cursor: not-allowed;
}

.status-box {
  margin-top: 15px;
  padding: 15px;
  border-radius: 4px;
  text-align: left;
}

.status-box.success {
  background-color: #f0f9ff;
  border: 1px solid #409eff;
  color: #409eff;
}

.status-box.error {
  background-color: #fef0f0;
  border: 1px solid #f56c6c;
  color: #f56c6c;
}

.status-box p {
  margin: 5px 0;
}

.nav-links {
  margin-top: 30px;
}

.nav-links a {
  display: inline-block;
  padding: 12px 24px;
  background-color: #409eff;
  color: white;
  text-decoration: none;
  border-radius: 4px;
  font-size: 16px;
}

.nav-links a:hover {
  background-color: #66b1ff;
}
</style>

<script setup>
</script>

<template>
  <div id="app">
    <nav class="navbar">
      <div class="navbar-brand">
        <router-link to="/">学生成绩系统</router-link>
      </div>
      <div class="navbar-menu">
        <router-link to="/">首页</router-link>
        <router-link to="/students">学生管理</router-link>
        <router-link to="/subjects">学科配置</router-link>
        <router-link to="/exam-types">考试类型配置</router-link>
        <router-link to="/score-entry">成绩录入</router-link>
      </div>
    </nav>
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.navbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 60px;
  background-color: #409eff;
  color: white;
}

.navbar-brand a {
  font-size: 18px;
  font-weight: bold;
  color: white;
  text-decoration: none;
}

.navbar-menu {
  display: flex;
  gap: 20px;
}

.navbar-menu a {
  color: rgba(255, 255, 255, 0.85);
  text-decoration: none;
  padding: 8px 12px;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.navbar-menu a:hover,
.navbar-menu a.router-link-active {
  background-color: rgba(255, 255, 255, 0.2);
  color: white;
}

.main-content {
  flex: 1;
  padding: 20px;
  background-color: #f5f7fa;
}
</style>

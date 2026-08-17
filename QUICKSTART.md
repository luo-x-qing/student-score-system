# 快速启动指南

## 问题已修复 ✅

**问题：** 后端启动失败（尝试连接 MySQL）  
**修复：** 已改用 H2 内存数据库

## 启动步骤

### 1. 启动后端

打开 PowerShell 或 CMD，进入后端目录：

```bash
cd student-score-backend
./mvnw spring-boot:run
```

**等待启动**（约 30-60 秒），看到以下日志表示成功：
```
Started StudentScoreBackendApplication in X.XXX seconds
```

**验证后端：**
- 浏览器访问：http://localhost:8080/health
- 或访问 H2 控制台：http://localhost:8080/h2-console

### 2. 启动前端

**在另一个窗口**，进入前端目录：

```bash
cd student-score-frontend
npm run dev
```

前端将在 **http://localhost:5173** 启动

### 3. 访问系统

浏览器打开：**http://localhost:5173**

## 快速功能测试

### 第一步：配置基础数据（必须先完成）

1. **学科配置**
   - 点击「学科配置」
   - 新增：语文（权重1.5）、数学（权重1.5）、英语（权重1.0）

2. **考试类型配置**
   - 点击「考试类型配置」
   - 新增：期中考试（30%）、期末考试（50%）、平时成绩（20%）
   - 验证总比率显示 100%（绿色）

3. **学生管理**
   - 点击「学生管理」
   - 新增：2024001 张三 一年级1班

### 第二步：成绩录入

1. 点击「成绩录入」
2. 选择：一年级1班 + 语文 + 期中考试
3. 点击「加载成绩」
4. 为张三录入成绩：85
5. 点击「批量保存」

### 第三步：查看排名

1. 点击「成绩排名」
2. 选择「按学科排名」
3. 选择学科：语文
4. 点击「查询排名」

## 常见问题

### Q: 后端启动失败
**A:** 检查端口 8080 是否被占用：
```bash
netstat -ano | findstr :8080
```

### Q: 前端连接后端失败
**A:** 
1. 确保后端已启动（检查 http://localhost:8080/health）
2. 检查前端 vite.config.js 中的 proxy 配置

### Q: 数据丢失了
**A:** H2 内存数据库，重启后数据会丢失（这是正常的）

## 数据库配置

当前使用 **H2 内存数据库**：
- 优点：无需安装 MySQL，即开即用
- 缺点：重启后数据丢失

### 如果需要持久化（可选）

修改 `application.properties`：

```properties
# 改为文件模式（数据持久化）
spring.datasource.url=jdbc:h2:file:./data/student_score

# 或改用 MySQL（需要先安装 MySQL）
spring.datasource.url=jdbc:mysql://localhost:3306/student_score
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

## 技术支持

- 完整文档：[PROJECT_SUMMARY.md](./PROJECT_SUMMARY.md)
- 测试清单：[DEPLOYMENT_TEST.md](./DEPLOYMENT_TEST.md)
- 部署说明：[README_DEPLOYMENT.md](./README_DEPLOYMENT.md)

---

**祝您使用愉快！** 🎉

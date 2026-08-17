package com.ecommerce.studentscorebackend.controller;

import com.ecommerce.studentscorebackend.common.ApiResponse;
import com.ecommerce.studentscorebackend.dto.StudentCreateRequest;
import com.ecommerce.studentscorebackend.entity.Student;
import com.ecommerce.studentscorebackend.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 学生管理控制器
 *
 * 提供学生信息的REST API接口
 * 基础路径：/students
 *
 * API列表：
 * - POST /students - 创建学生
 * - GET /students/{id} - 查询学生详情
 * - PUT /students/{id} - 更新学生信息
 * - DELETE /students/{id} - 删除学生
 * - GET /students - 查询学生列表（支持搜索和分页）
 */
@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    /**
     * 创建新学生
     *
     * 请求方式：POST /students
     * 请求体：JSON格式的学生信息
     *
     * 业务规则：
     * 1. 学号去除首尾空格后必须非空且唯一
     * 2. 姓名和班级必填
     * 3. 学号重复时返回409冲突错误
     *
     * @param request 学生创建请求（自动校验）
     * @return 创建成功的学生信息
     */
    @PostMapping
    public ApiResponse<Student> createStudent(@Valid @RequestBody StudentCreateRequest request) {
        try {
            // 调用业务层创建学生
            Student student = studentService.createStudent(request);
            return ApiResponse.success(student);
        } catch (IllegalArgumentException e) {
            // 捕获学号重复等业务异常，返回409冲突
            return ApiResponse.conflict(e.getMessage());
        } catch (Exception e) {
            // 捕获其他异常，返回500服务器错误
            return ApiResponse.error(500, "创建学生失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID查询学生详情
     *
     * 请求方式：GET /students/{id}
     *
     * @param id 学生ID
     * @return 学生详细信息，不存在时返回404
     */
    @GetMapping("/{id}")
    public ApiResponse<Student> getStudent(@PathVariable Long id) {
        Student student = studentService.getStudentById(id);
        if (student == null) {
            return ApiResponse.notFound("学生不存在，ID: " + id);
        }
        return ApiResponse.success(student);
    }
}

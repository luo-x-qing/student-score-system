package com.ecommerce.studentscorebackend.controller;

import com.ecommerce.studentscorebackend.common.ApiResponse;
import com.ecommerce.studentscorebackend.dto.PageResponse;
import com.ecommerce.studentscorebackend.dto.StudentCreateRequest;
import com.ecommerce.studentscorebackend.dto.StudentQueryRequest;
import com.ecommerce.studentscorebackend.dto.StudentUpdateRequest;
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

    /**
     * 更新学生信息
     *
     * 请求方式：PUT /students/{id}
     * 请求体：JSON格式的更新信息（不包含学号）
     *
     * 业务规则：
     * 1. 学号不允许修改
     * 2. 姓名和班级必填
     * 3. 学生不存在时返回404
     *
     * @param id 学生ID
     * @param request 更新请求（自动校验）
     * @return 更新后的学生信息
     */
    @PutMapping("/{id}")
    public ApiResponse<Student> updateStudent(@PathVariable Long id, @Valid @RequestBody StudentUpdateRequest request) {
        try {
            Student student = studentService.updateStudent(id, request);
            return ApiResponse.success(student);
        } catch (IllegalArgumentException e) {
            // 学生不存在，返回404
            return ApiResponse.notFound(e.getMessage());
        } catch (Exception e) {
            // 其他异常，返回500
            return ApiResponse.error(500, "更新学生失败: " + e.getMessage());
        }
    }

    /**
     * 删除学生
     *
     * 请求方式：DELETE /students/{id}
     * 查询参数：cascade（可选，默认false）
     *
     * 业务规则：
     * 1. 学生不存在时返回404
     * 2. 已有成绩时：
     *    - cascade=false（默认）：返回409冲突错误
     *    - cascade=true：同时删除成绩（事务保证一致性）
     *
     * @param id 学生ID
     * @param cascade 是否级联删除成绩（默认false）
     * @return 删除成功的消息
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteStudent(@PathVariable Long id, @RequestParam(defaultValue = "false") boolean cascade) {
        try {
            studentService.deleteStudent(id, cascade);
            return ApiResponse.success();
        } catch (IllegalArgumentException e) {
            // 学生不存在或成绩冲突
            String message = e.getMessage();
            if (message.contains("不存在")) {
                return ApiResponse.notFound(message);
            } else {
                return ApiResponse.conflict(message);
            }
        } catch (Exception e) {
            // 其他异常，返回500
            return ApiResponse.error(500, "删除学生失败: " + e.getMessage());
        }
    }

    /**
     * 查询学生列表（支持搜索和分页）
     *
     * 请求方式：GET /students
     * 查询参数：
     * - studentNo: 学号（支持前缀匹配）
     * - name: 姓名（支持包含匹配）
     * - className: 班级（精确匹配）
     * - page: 页码（默认1）
     * - pageSize: 每页记录数（默认20，最大100）
     *
     * @param request 查询请求（自动绑定查询参数）
     * @return 分页结果
     */
    @GetMapping
    public ApiResponse<PageResponse<Student>> queryStudents(StudentQueryRequest request) {
        try {
            PageResponse<Student> result = studentService.queryStudents(request);
            return ApiResponse.success(result);
        } catch (Exception e) {
            // 查询异常，返回500
            return ApiResponse.error(500, "查询学生列表失败: " + e.getMessage());
        }
    }
}

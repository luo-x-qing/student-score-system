package com.ecommerce.studentscorebackend.controller;

import com.ecommerce.studentscorebackend.common.ApiResponse;
import com.ecommerce.studentscorebackend.dto.SubjectCreateRequest;
import com.ecommerce.studentscorebackend.dto.SubjectUpdateRequest;
import com.ecommerce.studentscorebackend.entity.Subject;
import com.ecommerce.studentscorebackend.service.SubjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学科管理控制器
 *
 * 提供学科信息的REST API接口
 * 基础路径：/subjects
 *
 * API列表：
 * - POST /subjects - 创建学科
 * - GET /subjects/{id} - 查询学科详情
 * - PUT /subjects/{id} - 更新学科信息
 * - DELETE /subjects/{id} - 删除学科
 * - GET /subjects - 查询所有学科列表
 */
@RestController
@RequestMapping("/subjects")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    /**
     * 创建新学科
     *
     * 请求方式：POST /subjects
     * 请求体：JSON格式的学科信息
     *
     * 业务规则：
     * 1. 学科名称必须全局唯一
     * 2. 权重必须为正数
     * 3. 学科名称重复时返回409冲突错误
     *
     * @param request 学科创建请求（自动校验）
     * @return 创建成功的学科信息
     */
    @PostMapping
    public ApiResponse<Subject> createSubject(@Valid @RequestBody SubjectCreateRequest request) {
        try {
            Subject subject = subjectService.createSubject(request);
            return ApiResponse.success(subject);
        } catch (IllegalArgumentException e) {
            // 捕获学科名称重复等业务异常，返回409冲突
            return ApiResponse.conflict(e.getMessage());
        } catch (Exception e) {
            // 捕获其他异常，返回500服务器错误
            return ApiResponse.error(500, "创建学科失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID查询学科详情
     *
     * 请求方式：GET /subjects/{id}
     *
     * @param id 学科ID
     * @return 学科详细信息，不存在时返回404
     */
    @GetMapping("/{id}")
    public ApiResponse<Subject> getSubject(@PathVariable Long id) {
        Subject subject = subjectService.getSubjectById(id);
        if (subject == null) {
            return ApiResponse.notFound("学科不存在，ID: " + id);
        }
        return ApiResponse.success(subject);
    }

    /**
     * 更新学科信息
     *
     * 请求方式：PUT /subjects/{id}
     * 请求体：JSON格式的更新信息
     *
     * 业务规则：
     * 1. 学科名称不能与其他学科重复
     * 2. 权重必须为正数
     * 3. 学科不存在时返回404
     *
     * @param id 学科ID
     * @param request 更新请求（自动校验）
     * @return 更新后的学科信息
     */
    @PutMapping("/{id}")
    public ApiResponse<Subject> updateSubject(@PathVariable Long id, @Valid @RequestBody SubjectUpdateRequest request) {
        try {
            Subject subject = subjectService.updateSubject(id, request);
            return ApiResponse.success(subject);
        } catch (IllegalArgumentException e) {
            // 学科不存在或名称重复
            String message = e.getMessage();
            if (message.contains("不存在")) {
                return ApiResponse.notFound(message);
            } else {
                return ApiResponse.conflict(message);
            }
        } catch (Exception e) {
            // 其他异常，返回500
            return ApiResponse.error(500, "更新学科失败: " + e.getMessage());
        }
    }

    /**
     * 删除学科
     *
     * 请求方式：DELETE /subjects/{id}
     *
     * 业务规则：
     * 1. 学科不存在时返回404
     * 2. 已被成绩引用的学科不允许删除，返回409冲突
     *
     * @param id 学科ID
     * @return 删除成功的消息
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteSubject(@PathVariable Long id) {
        try {
            subjectService.deleteSubject(id);
            return ApiResponse.success();
        } catch (IllegalArgumentException e) {
            // 学科不存在或有成绩引用
            String message = e.getMessage();
            if (message.contains("不存在")) {
                return ApiResponse.notFound(message);
            } else {
                return ApiResponse.conflict(message);
            }
        } catch (Exception e) {
            // 其他异常，返回500
            return ApiResponse.error(500, "删除学科失败: " + e.getMessage());
        }
    }

    /**
     * 查询所有学科列表
     *
     * 请求方式：GET /subjects
     *
     * 返回所有学科，按学科名称升序排序
     *
     * @return 学科列表
     */
    @GetMapping
    public ApiResponse<List<Subject>> getAllSubjects() {
        try {
            List<Subject> subjects = subjectService.getAllSubjects();
            return ApiResponse.success(subjects);
        } catch (Exception e) {
            // 查询异常，返回500
            return ApiResponse.error(500, "查询学科列表失败: " + e.getMessage());
        }
    }
}

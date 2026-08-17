package com.ecommerce.studentscorebackend.controller;

import com.ecommerce.studentscorebackend.common.ApiResponse;
import com.ecommerce.studentscorebackend.dto.ExamTypeCreateRequest;
import com.ecommerce.studentscorebackend.dto.ExamTypeUpdateRequest;
import com.ecommerce.studentscorebackend.entity.ExamType;
import com.ecommerce.studentscorebackend.service.ExamTypeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 考试类型管理控制器
 *
 * 提供考试类型信息的REST API接口
 * 基础路径：/exam-types
 *
 * API列表：
 * - POST /exam-types - 创建考试类型
 * - GET /exam-types/{id} - 查询考试类型详情
 * - PUT /exam-types/{id} - 更新考试类型信息
 * - DELETE /exam-types/{id} - 删除考试类型
 * - GET /exam-types - 查询所有考试类型列表
 */
@RestController
@RequestMapping("/exam-types")
public class ExamTypeController {

    @Autowired
    private ExamTypeService examTypeService;

    /**
     * 创建新考试类型
     *
     * 请求方式：POST /exam-types
     * 请求体：JSON格式的考试类型信息
     *
     * 业务规则：
     * 1. 考试类型名称必须全局唯一
     * 2. 比率必须在 0-100 之间
     * 3. 考试类型名称重复时返回409冲突错误
     *
     * @param request 考试类型创建请求（自动校验）
     * @return 创建成功的考试类型信息
     */
    @PostMapping
    public ApiResponse<ExamType> createExamType(@Valid @RequestBody ExamTypeCreateRequest request) {
        try {
            ExamType examType = examTypeService.createExamType(request);
            return ApiResponse.success(examType);
        } catch (IllegalArgumentException e) {
            // 捕获考试类型名称重复等业务异常，返回409冲突
            return ApiResponse.conflict(e.getMessage());
        } catch (Exception e) {
            // 捕获其他异常，返回500服务器错误
            return ApiResponse.error(500, "创建考试类型失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID查询考试类型详情
     *
     * 请求方式：GET /exam-types/{id}
     *
     * @param id 考试类型ID
     * @return 考试类型详细信息，不存在时返回404
     */
    @GetMapping("/{id}")
    public ApiResponse<ExamType> getExamType(@PathVariable Long id) {
        ExamType examType = examTypeService.getExamTypeById(id);
        if (examType == null) {
            return ApiResponse.notFound("考试类型不存在，ID: " + id);
        }
        return ApiResponse.success(examType);
    }

    /**
     * 更新考试类型信息
     *
     * 请求方式：PUT /exam-types/{id}
     * 请求体：JSON格式的更新信息
     *
     * 业务规则：
     * 1. 考试类型名称不能与其他考试类型重复
     * 2. 比率必须在 0-100 之间
     * 3. 考试类型不存在时返回404
     *
     * @param id 考试类型ID
     * @param request 更新请求（自动校验）
     * @return 更新后的考试类型信息
     */
    @PutMapping("/{id}")
    public ApiResponse<ExamType> updateExamType(@PathVariable Long id, @Valid @RequestBody ExamTypeUpdateRequest request) {
        try {
            ExamType examType = examTypeService.updateExamType(id, request);
            return ApiResponse.success(examType);
        } catch (IllegalArgumentException e) {
            // 考试类型不存在或名称重复
            String message = e.getMessage();
            if (message.contains("不存在")) {
                return ApiResponse.notFound(message);
            } else {
                return ApiResponse.conflict(message);
            }
        } catch (Exception e) {
            // 其他异常，返回500
            return ApiResponse.error(500, "更新考试类型失败: " + e.getMessage());
        }
    }

    /**
     * 删除考试类型
     *
     * 请求方式：DELETE /exam-types/{id}
     *
     * 业务规则：
     * 1. 考试类型不存在时返回404
     * 2. 已被成绩引用的考试类型不允许删除，返回409冲突
     *
     * @param id 考试类型ID
     * @return 删除成功的消息
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteExamType(@PathVariable Long id) {
        try {
            examTypeService.deleteExamType(id);
            return ApiResponse.success();
        } catch (IllegalArgumentException e) {
            // 考试类型不存在或有成绩引用
            String message = e.getMessage();
            if (message.contains("不存在")) {
                return ApiResponse.notFound(message);
            } else {
                return ApiResponse.conflict(message);
            }
        } catch (Exception e) {
            // 其他异常，返回500
            return ApiResponse.error(500, "删除考试类型失败: " + e.getMessage());
        }
    }

    /**
     * 查询所有考试类型列表
     *
     * 请求方式：GET /exam-types
     *
     * 返回所有考试类型，按考试类型名称升序排序
     *
     * @return 考试类型列表
     */
    @GetMapping
    public ApiResponse<List<ExamType>> getAllExamTypes() {
        try {
            List<ExamType> examTypes = examTypeService.getAllExamTypes();
            return ApiResponse.success(examTypes);
        } catch (Exception e) {
            // 查询异常，返回500
            return ApiResponse.error(500, "查询考试类型列表失败: " + e.getMessage());
        }
    }
}

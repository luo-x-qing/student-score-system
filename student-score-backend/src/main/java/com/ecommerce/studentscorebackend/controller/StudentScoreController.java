package com.ecommerce.studentscorebackend.controller;

import com.ecommerce.studentscorebackend.common.ApiResponse;
import com.ecommerce.studentscorebackend.dto.ScoreBatchSaveRequest;
import com.ecommerce.studentscorebackend.dto.ScoreSaveRequest;
import com.ecommerce.studentscorebackend.entity.StudentScore;
import com.ecommerce.studentscorebackend.service.StudentScoreService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学生成绩管理控制器
 *
 * 提供学生成绩信息的REST API接口
 * 基础路径：/scores
 *
 * API列表：
 * - POST /scores - 保存单条成绩
 * - POST /scores/batch - 批量保存成绩
 * - DELETE /scores/{id} - 删除成绩
 * - GET /scores/{id} - 根据ID查询成绩
 * - GET /scores/student/{studentId} - 查询指定学生的所有成绩
 * - GET /scores/subject/{subjectId} - 查询指定学科的所有成绩
 * - GET /scores/exam-type/{examTypeId} - 查询指定考试类型的所有成绩
 */
@RestController
@RequestMapping("/scores")
public class StudentScoreController {

    @Autowired
    private StudentScoreService studentScoreService;

    /**
     * 保存单条成绩
     *
     * 请求方式：POST /scores
     * 请求体：JSON格式的成绩信息
     *
     * 业务规则：
     * 1. 学生、学科、考试类型必须存在
     * 2. 学生ID + 学科ID + 考试类型ID 组合唯一
     * 3. 如果已存在相同组合的成绩，则更新；否则新增
     * 4. 成绩范围：0-100 或 null
     *
     * @param request 成绩保存请求（自动校验）
     * @return 保存后的成绩信息
     */
    @PostMapping
    public ApiResponse<StudentScore> saveScore(@Valid @RequestBody ScoreSaveRequest request) {
        try {
            StudentScore score = studentScoreService.saveScore(request);
            return ApiResponse.success(score);
        } catch (IllegalArgumentException e) {
            // 捕获外键不存在等业务异常
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            // 捕获其他异常，返回500服务器错误
            return ApiResponse.error(500, "保存成绩失败: " + e.getMessage());
        }
    }

    /**
     * 批量保存成绩
     *
     * 请求方式：POST /scores/batch
     * 请求体：JSON格式的成绩列表
     *
     * 业务规则：
     * 1. 所有成绩必须通过校验
     * 2. 使用事务保证整批保存的原子性（全部成功或全部失败）
     * 3. 如果任何一条失败，整批回滚
     *
     * @param request 批量成绩保存请求（自动校验）
     * @return 保存后的成绩列表
     */
    @PostMapping("/batch")
    public ApiResponse<List<StudentScore>> batchSaveScores(@Valid @RequestBody ScoreBatchSaveRequest request) {
        try {
            List<StudentScore> scores = studentScoreService.batchSaveScores(request);
            return ApiResponse.success(scores);
        } catch (IllegalArgumentException e) {
            // 捕获外键不存在等业务异常
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            // 捕获其他异常，返回500服务器错误
            return ApiResponse.error(500, "批量保存成绩失败: " + e.getMessage());
        }
    }

    /**
     * 删除成绩
     *
     * 请求方式：DELETE /scores/{id}
     *
     * @param id 成绩ID
     * @return 删除成功的消息
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteScore(@PathVariable Long id) {
        try {
            studentScoreService.deleteScore(id);
            return ApiResponse.success();
        } catch (IllegalArgumentException e) {
            // 成绩不存在
            return ApiResponse.notFound(e.getMessage());
        } catch (Exception e) {
            // 其他异常，返回500
            return ApiResponse.error(500, "删除成绩失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID查询成绩
     *
     * 请求方式：GET /scores/{id}
     *
     * @param id 成绩ID
     * @return 成绩详细信息，不存在时返回404
     */
    @GetMapping("/{id}")
    public ApiResponse<StudentScore> getScore(@PathVariable Long id) {
        StudentScore score = studentScoreService.getScoreById(id);
        if (score == null) {
            return ApiResponse.notFound("成绩不存在，ID: " + id);
        }
        return ApiResponse.success(score);
    }

    /**
     * 查询指定学生的所有成绩
     *
     * 请求方式：GET /scores/student/{studentId}
     *
     * @param studentId 学生ID
     * @return 成绩列表
     */
    @GetMapping("/student/{studentId}")
    public ApiResponse<List<StudentScore>> getScoresByStudent(@PathVariable Long studentId) {
        try {
            List<StudentScore> scores = studentScoreService.getScoresByStudentId(studentId);
            return ApiResponse.success(scores);
        } catch (Exception e) {
            return ApiResponse.error(500, "查询学生成绩失败: " + e.getMessage());
        }
    }

    /**
     * 查询指定学科的所有成绩
     *
     * 请求方式：GET /scores/subject/{subjectId}
     *
     * @param subjectId 学科ID
     * @return 成绩列表
     */
    @GetMapping("/subject/{subjectId}")
    public ApiResponse<List<StudentScore>> getScoresBySubject(@PathVariable Long subjectId) {
        try {
            List<StudentScore> scores = studentScoreService.getScoresBySubjectId(subjectId);
            return ApiResponse.success(scores);
        } catch (Exception e) {
            return ApiResponse.error(500, "查询学科成绩失败: " + e.getMessage());
        }
    }

    /**
     * 查询指定考试类型的所有成绩
     *
     * 请求方式：GET /scores/exam-type/{examTypeId}
     *
     * @param examTypeId 考试类型ID
     * @return 成绩列表
     */
    @GetMapping("/exam-type/{examTypeId}")
    public ApiResponse<List<StudentScore>> getScoresByExamType(@PathVariable Long examTypeId) {
        try {
            List<StudentScore> scores = studentScoreService.getScoresByExamTypeId(examTypeId);
            return ApiResponse.success(scores);
        } catch (Exception e) {
            return ApiResponse.error(500, "查询考试类型成绩失败: " + e.getMessage());
        }
    }
}

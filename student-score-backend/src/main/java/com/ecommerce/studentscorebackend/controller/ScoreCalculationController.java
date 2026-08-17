package com.ecommerce.studentscorebackend.controller;

import com.ecommerce.studentscorebackend.common.ApiResponse;
import com.ecommerce.studentscorebackend.dto.ComprehensiveScoreResponse;
import com.ecommerce.studentscorebackend.dto.SubjectTotalScoreResponse;
import com.ecommerce.studentscorebackend.service.ScoreCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 总评计算控制器
 *
 * 提供总评计算的REST API接口
 * 基础路径：/score-calculation
 *
 * API列表：
 * - GET /score-calculation/subject/{studentId}/{subjectId} - 计算学科总评
 * - GET /score-calculation/comprehensive/{studentId} - 计算综合得分
 */
@RestController
@RequestMapping("/score-calculation")
public class ScoreCalculationController {

    @Autowired
    private ScoreCalculationService scoreCalculationService;

    /**
     * 计算学生在某个学科的总评成绩
     *
     * 请求方式：GET /score-calculation/subject/{studentId}/{subjectId}
     *
     * @param studentId 学生ID
     * @param subjectId 学科ID
     * @return 学科总评响应，包含总评成绩和是否完整标识
     */
    @GetMapping("/subject/{studentId}/{subjectId}")
    public ApiResponse<SubjectTotalScoreResponse> calculateSubjectTotalScore(
            @PathVariable Long studentId,
            @PathVariable Long subjectId) {
        try {
            SubjectTotalScoreResponse response = scoreCalculationService.calculateSubjectTotalScore(studentId, subjectId);
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "计算学科总评失败: " + e.getMessage());
        }
    }

    /**
     * 计算学生的综合得分
     *
     * 请求方式：GET /score-calculation/comprehensive/{studentId}
     *
     * @param studentId 学生ID
     * @return 综合得分响应，包含综合得分、是否完整标识和各学科总评明细
     */
    @GetMapping("/comprehensive/{studentId}")
    public ApiResponse<ComprehensiveScoreResponse> calculateComprehensiveScore(@PathVariable Long studentId) {
        try {
            ComprehensiveScoreResponse response = scoreCalculationService.calculateComprehensiveScore(studentId);
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error(500, "计算综合得分失败: " + e.getMessage());
        }
    }
}

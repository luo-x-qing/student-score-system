package com.ecommerce.studentscorebackend.controller;

import com.ecommerce.studentscorebackend.common.ApiResponse;
import com.ecommerce.studentscorebackend.dto.RankingResponse;
import com.ecommerce.studentscorebackend.service.RankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 排名控制器
 *
 * 提供排名的REST API接口
 * 基础路径：/ranking
 *
 * API列表：
 * - GET /ranking/subject/{subjectId} - 按学科总评排名
 * - GET /ranking/comprehensive - 按综合得分排名
 */
@RestController
@RequestMapping("/ranking")
public class RankingController {

    @Autowired
    private RankingService rankingService;

    /**
     * 按学科总评排名
     *
     * 请求方式：GET /ranking/subject/{subjectId}
     *
     * 查询参数：
     * - className: 班级名称（可选）
     * - ascending: 是否升序（可选，默认 false 降序）
     *
     * @param subjectId 学科ID
     * @param className 班级名称（可选）
     * @param ascending 是否升序排序（可选，默认降序）
     * @return 排名列表
     */
    @GetMapping("/subject/{subjectId}")
    public ApiResponse<List<RankingResponse>> rankBySubjectTotalScore(
            @PathVariable Long subjectId,
            @RequestParam(required = false) String className,
            @RequestParam(required = false, defaultValue = "false") Boolean ascending) {
        try {
            List<RankingResponse> rankings = rankingService.rankBySubjectTotalScore(subjectId, className, ascending);
            return ApiResponse.success(rankings);
        } catch (Exception e) {
            return ApiResponse.error(500, "查询学科排名失败: " + e.getMessage());
        }
    }

    /**
     * 按综合得分排名
     *
     * 请求方式：GET /ranking/comprehensive
     *
     * 查询参数：
     * - className: 班级名称（可选）
     * - ascending: 是否升序（可选，默认 false 降序）
     *
     * @param className 班级名称（可选）
     * @param ascending 是否升序排序（可选，默认降序）
     * @return 排名列表
     */
    @GetMapping("/comprehensive")
    public ApiResponse<List<RankingResponse>> rankByComprehensiveScore(
            @RequestParam(required = false) String className,
            @RequestParam(required = false, defaultValue = "false") Boolean ascending) {
        try {
            List<RankingResponse> rankings = rankingService.rankByComprehensiveScore(className, ascending);
            return ApiResponse.success(rankings);
        } catch (Exception e) {
            return ApiResponse.error(500, "查询综合排名失败: " + e.getMessage());
        }
    }
}

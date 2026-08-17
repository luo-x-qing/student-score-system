package com.ecommerce.studentscorebackend.service;

import com.ecommerce.studentscorebackend.dto.RankingResponse;

import java.util.List;

/**
 * 排名服务接口
 *
 * 职责：
 * 1. 按学科总评排名
 * 2. 按综合得分排名
 * 3. 使用稠密排名算法（Dense Rank）：1、1、2（不跳号）
 * 4. 支持班级数据隔离
 * 5. 未完成成绩的学生不参与排名（rank 为 null）
 */
public interface RankingService {

    /**
     * 按学科总评排名
     *
     * 排名规则：
     * 1. 按学科总评降序排序（分数高的排前面）
     * 2. 使用稠密排名（Dense Rank）：相同分数的学生排名相同，下一个排名不跳号
     *    例如：100分2人排名1，90分1人排名2（不是3）
     * 3. 未完成成绩的学生（总评为null）不参与排名，rank 为 null，排在最后
     * 4. 支持按班级筛选（可选）
     *
     * @param subjectId 学科ID
     * @param className 班级名称（可选，null 表示全部班级）
     * @param ascending 是否升序排序（true=升序，false=降序，默认降序）
     * @return 排名列表
     */
    List<RankingResponse> rankBySubjectTotalScore(Long subjectId, String className, Boolean ascending);

    /**
     * 按综合得分排名
     *
     * 排名规则：
     * 1. 按综合得分降序排序（分数高的排前面）
     * 2. 使用稠密排名（Dense Rank）：相同分数的学生排名相同，下一个排名不跳号
     * 3. 未完成成绩的学生（综合得分为null）不参与排名，rank 为 null，排在最后
     * 4. 支持按班级筛选（可选）
     *
     * @param className 班级名称（可选，null 表示全部班级）
     * @param ascending 是否升序排序（true=升序，false=降序，默认降序）
     * @return 排名列表
     */
    List<RankingResponse> rankByComprehensiveScore(String className, Boolean ascending);
}

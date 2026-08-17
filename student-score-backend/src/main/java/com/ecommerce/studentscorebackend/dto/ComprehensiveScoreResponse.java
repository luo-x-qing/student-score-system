package com.ecommerce.studentscorebackend.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * 综合得分响应DTO
 *
 * 用于返回学生的综合得分
 * 综合得分计算公式：综合得分 = Σ(学科总评 × 学科权重)
 */
public class ComprehensiveScoreResponse {

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 学生姓名
     */
    private String studentName;

    /**
     * 综合得分
     * 计算规则：
     * 1. 如果所有学科的总评都完整，则计算综合得分
     * 2. 如果缺少任何一个学科的总评，则综合得分为 null
     */
    private BigDecimal comprehensiveScore;

    /**
     * 是否完整（所有学科都有总评）
     */
    private Boolean complete;

    /**
     * 各学科总评明细
     */
    private List<SubjectTotalScoreResponse> subjectScores;

    // Getters and Setters

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public BigDecimal getComprehensiveScore() {
        return comprehensiveScore;
    }

    public void setComprehensiveScore(BigDecimal comprehensiveScore) {
        this.comprehensiveScore = comprehensiveScore;
    }

    public Boolean getComplete() {
        return complete;
    }

    public void setComplete(Boolean complete) {
        this.complete = complete;
    }

    public List<SubjectTotalScoreResponse> getSubjectScores() {
        return subjectScores;
    }

    public void setSubjectScores(List<SubjectTotalScoreResponse> subjectScores) {
        this.subjectScores = subjectScores;
    }
}

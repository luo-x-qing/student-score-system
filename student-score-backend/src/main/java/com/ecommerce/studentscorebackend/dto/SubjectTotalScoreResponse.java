package com.ecommerce.studentscorebackend.dto;

import java.math.BigDecimal;

/**
 * 学科总评响应DTO
 *
 * 用于返回学生在某个学科的总评成绩
 * 总评计算公式：学科总评 = Σ(考试成绩 × 考试类型比率%)
 */
public class SubjectTotalScoreResponse {

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 学科ID
     */
    private Long subjectId;

    /**
     * 学科名称
     */
    private String subjectName;

    /**
     * 学科总评成绩
     * 计算规则：
     * 1. 如果所有考试类型都有成绩，则计算总评
     * 2. 如果缺少任何一个考试类型的成绩，则总评为 null
     */
    private BigDecimal totalScore;

    /**
     * 是否完整（所有考试类型都有成绩）
     */
    private Boolean complete;

    // Getters and Setters

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public BigDecimal getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(BigDecimal totalScore) {
        this.totalScore = totalScore;
    }

    public Boolean getComplete() {
        return complete;
    }

    public void setComplete(Boolean complete) {
        this.complete = complete;
    }
}

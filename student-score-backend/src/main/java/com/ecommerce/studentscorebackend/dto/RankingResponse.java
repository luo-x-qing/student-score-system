package com.ecommerce.studentscorebackend.dto;

import java.math.BigDecimal;

/**
 * 排名响应DTO
 *
 * 用于返回学生的排名信息
 */
public class RankingResponse {

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 学号
     */
    private String studentNo;

    /**
     * 学生姓名
     */
    private String studentName;

    /**
     * 班级
     */
    private String className;

    /**
     * 成绩（学科总评或综合得分）
     */
    private BigDecimal score;

    /**
     * 排名
     * 使用稠密排名（Dense Rank）：1、1、2（不跳号）
     * 如果成绩为 null（未完成），则排名为 null
     */
    private Integer rank;

    /**
     * 是否完成（成绩是否完整）
     */
    private Boolean complete;

    // Getters and Setters

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Boolean getComplete() {
        return complete;
    }

    public void setComplete(Boolean complete) {
        this.complete = complete;
    }
}

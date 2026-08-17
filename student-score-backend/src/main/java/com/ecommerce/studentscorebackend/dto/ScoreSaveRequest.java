package com.ecommerce.studentscorebackend.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * 成绩保存请求DTO（单条）
 *
 * 用于接收前端保存单条成绩的请求数据
 */
public class ScoreSaveRequest {

    /**
     * 学生ID，必填
     */
    @NotNull(message = "学生ID不能为空")
    private Long studentId;

    /**
     * 学科ID，必填
     */
    @NotNull(message = "学科ID不能为空")
    private Long subjectId;

    /**
     * 考试类型ID，必填
     */
    @NotNull(message = "考试类型ID不能为空")
    private Long examTypeId;

    /**
     * 成绩，可为 null（表示缺考或未录入）
     * 如果不为 null，则范围必须在 0-100
     */
    @DecimalMin(value = "0", message = "成绩不能小于0")
    @DecimalMax(value = "100", message = "成绩不能大于100")
    private BigDecimal score;

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

    public Long getExamTypeId() {
        return examTypeId;
    }

    public void setExamTypeId(Long examTypeId) {
        this.examTypeId = examTypeId;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }
}

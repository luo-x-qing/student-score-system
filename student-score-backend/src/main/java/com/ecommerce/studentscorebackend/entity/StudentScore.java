package com.ecommerce.studentscorebackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学生成绩实体类
 * 对应数据库表 student_score
 *
 * 业务规则：
 * 1. 学生ID、学科ID、考试类型ID 组合唯一（一个学生在某个学科的某种考试类型下只有一个成绩）
 * 2. 成绩范围：0-100 或 null（null表示缺考/未录入）
 * 3. 删除学生、学科或考试类型时，如果有成绩引用，不允许删除
 */
@TableName("student_score")
public class StudentScore {

    /**
     * 主键ID，数据库自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 学生ID，关联 student 表
     */
    @NotNull(message = "学生ID不能为空")
    private Long studentId;

    /**
     * 学科ID，关联 subject 表
     */
    @NotNull(message = "学科ID不能为空")
    private Long subjectId;

    /**
     * 考试类型ID，关联 exam_type 表
     */
    @NotNull(message = "考试类型ID不能为空")
    private Long examTypeId;

    /**
     * 成绩，范围：0-100 或 null
     * null 表示缺考或未录入
     */
    @DecimalMin(value = "0", message = "成绩不能小于0")
    @DecimalMax(value = "100", message = "成绩不能大于100")
    private BigDecimal score;

    /**
     * 创建时间，数据库自动生成
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间，数据库自动维护
     */
    private LocalDateTime updatedAt;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

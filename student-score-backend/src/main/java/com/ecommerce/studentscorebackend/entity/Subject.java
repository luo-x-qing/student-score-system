package com.ecommerce.studentscorebackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学科实体类
 * 对应数据库表 subject
 *
 * 业务规则：
 * 1. 学科名称必须唯一
 * 2. 权重必须为正数（> 0）
 * 3. 已被成绩引用的学科不允许删除
 */
@TableName("subject")
public class Subject {

    /**
     * 主键ID，数据库自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 学科名称，全局唯一
     * 例如：语文、数学、英语
     */
    @NotBlank(message = "学科名称不能为空")
    @Size(max = 64, message = "学科名称长度不能超过64个字符")
    private String subjectName;

    /**
     * 权重系数，必须为正数
     * 用于计算综合得分：综合得分 = Σ(学科总评 × 权重)
     *
     * 例如：
     * - 主科权重可以设置为 1.5
     * - 副科权重可以设置为 1.0
     */
    @NotNull(message = "权重不能为空")
    @Positive(message = "权重必须为正数")
    private BigDecimal weightRate;

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

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public BigDecimal getWeightRate() {
        return weightRate;
    }

    public void setWeightRate(BigDecimal weightRate) {
        this.weightRate = weightRate;
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

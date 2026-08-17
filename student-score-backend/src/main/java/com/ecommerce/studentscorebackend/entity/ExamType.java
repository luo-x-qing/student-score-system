package com.ecommerce.studentscorebackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 考试类型实体类
 * 对应数据库表 exam_type
 *
 * 业务规则：
 * 1. 考试类型名称必须唯一
 * 2. 比率必须在 0-100 之间（包含边界）
 * 3. 已被成绩引用的考试类型不允许删除
 */
@TableName("exam_type")
public class ExamType {

    /**
     * 主键ID，数据库自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 考试类型名称，全局唯一
     * 例如：期中考试、期末考试、平时成绩
     */
    @NotBlank(message = "考试类型名称不能为空")
    @Size(max = 64, message = "考试类型名称长度不能超过64个字符")
    private String typeName;

    /**
     * 比率（百分比），范围：0-100
     * 用于计算学科总评：学科总评 = Σ(考试成绩 × 比率%)
     *
     * 例如：
     * - 期中考试：30%
     * - 期末考试：50%
     * - 平时成绩：20%
     * 总和应为 100%
     */
    @NotNull(message = "比率不能为空")
    @DecimalMin(value = "0", message = "比率不能小于0")
    @DecimalMax(value = "100", message = "比率不能大于100")
    private BigDecimal rate;

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

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
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

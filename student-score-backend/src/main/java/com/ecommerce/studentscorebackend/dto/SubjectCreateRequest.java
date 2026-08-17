package com.ecommerce.studentscorebackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * 学科创建请求DTO
 *
 * 用于接收前端创建学科的请求数据
 */
public class SubjectCreateRequest {

    /**
     * 学科名称，必填，全局唯一
     */
    @NotBlank(message = "学科名称不能为空")
    @Size(max = 64, message = "学科名称长度不能超过64个字符")
    private String subjectName;

    /**
     * 权重系数，必填，必须为正数
     */
    @NotNull(message = "权重不能为空")
    @Positive(message = "权重必须为正数")
    private BigDecimal weightRate;

    // Getters and Setters

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
}

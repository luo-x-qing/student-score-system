package com.ecommerce.studentscorebackend.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * 考试类型创建请求DTO
 *
 * 用于接收前端创建考试类型的请求数据
 */
public class ExamTypeCreateRequest {

    /**
     * 考试类型名称，必填，全局唯一
     */
    @NotBlank(message = "考试类型名称不能为空")
    @Size(max = 64, message = "考试类型名称长度不能超过64个字符")
    private String typeName;

    /**
     * 比率（百分比），必填，范围：0-100
     */
    @NotNull(message = "比率不能为空")
    @DecimalMin(value = "0", message = "比率不能小于0")
    @DecimalMax(value = "100", message = "比率不能大于100")
    private BigDecimal rate;

    // Getters and Setters

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
}

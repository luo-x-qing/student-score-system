package com.ecommerce.studentscorebackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 学生更新请求DTO
 *
 * 用于接收前端更新学生信息的请求数据
 * 学号不允许修改（通过ID定位学生）
 */
public class StudentUpdateRequest {

    /**
     * 姓名，必填
     */
    @NotBlank(message = "姓名不能为空")
    @Size(max = 64, message = "姓名长度不能超过64个字符")
    private String name;

    /**
     * 性别，可选
     */
    @Size(max = 10, message = "性别长度不能超过10个字符")
    private String gender;

    /**
     * 班级名称，必填
     */
    @NotBlank(message = "班级不能为空")
    @Size(max = 64, message = "班级名称长度不能超过64个字符")
    private String className;

    /**
     * 备注，可选
     */
    private String remarks;

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}

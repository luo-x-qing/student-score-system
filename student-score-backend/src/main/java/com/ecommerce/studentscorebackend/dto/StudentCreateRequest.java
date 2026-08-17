package com.ecommerce.studentscorebackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 学生创建请求DTO
 *
 * 用于接收前端创建学生的请求数据
 * 包含所有必填和可选字段的校验规则
 */
public class StudentCreateRequest {

    /**
     * 学号，必填，全局唯一
     * 去除首尾空格后长度1-32个字符
     */
    @NotBlank(message = "学号不能为空")
    @Size(min = 1, max = 32, message = "学号长度必须在1-32个字符之间")
    private String studentNo;

    /**
     * 姓名，必填
     */
    @NotBlank(message = "姓名不能为空")
    @Size(max = 64, message = "姓名长度不能超过64个字符")
    private String name;

    /**
     * 性别，可选
     * 示例：男、女、未知
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

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

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

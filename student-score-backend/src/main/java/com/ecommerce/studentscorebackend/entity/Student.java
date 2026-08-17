package com.ecommerce.studentscorebackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * 学生实体类
 * 对应数据库表 student
 *
 * 业务规则：
 * 1. 学号去除首尾空格后必须非空、全局唯一，最长32个字符
 * 2. 姓名和班级必须填写
 * 3. 性别和备注为可选字段
 */
@TableName("student")
public class Student {

    /**
     * 主键ID，数据库自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 学号，全局唯一标识
     * 长度限制：1-32个字符（去除首尾空格后）
     */
    @NotBlank(message = "学号不能为空")
    @Size(max = 32, message = "学号长度不能超过32个字符")
    private String studentNo;

    /**
     * 学生姓名，必填
     */
    @NotBlank(message = "姓名不能为空")
    @Size(max = 64, message = "姓名长度不能超过64个字符")
    private String name;

    /**
     * 性别，可选字段
     * 示例值：男、女、未知
     */
    @Size(max = 10, message = "性别长度不能超过10个字符")
    private String gender;

    /**
     * 班级名称，必填
     * 本期不单独开发班级管理模块，以规范字符串保存
     */
    @NotBlank(message = "班级不能为空")
    @Size(max = 64, message = "班级名称长度不能超过64个字符")
    private String className;

    /**
     * 备注信息，可选
     */
    private String remarks;

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

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        // 去除首尾空格，符合PRD要求
        this.studentNo = studentNo != null ? studentNo.trim() : null;
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

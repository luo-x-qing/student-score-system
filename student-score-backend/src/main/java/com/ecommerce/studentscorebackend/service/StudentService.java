package com.ecommerce.studentscorebackend.service;

import com.ecommerce.studentscorebackend.dto.StudentCreateRequest;
import com.ecommerce.studentscorebackend.entity.Student;

/**
 * 学生业务逻辑接口
 *
 * 职责：
 * 1. 学生信息的增删改查
 * 2. 学号唯一性校验
 * 3. 删除前成绩冲突检查
 * 4. 查询、搜索和分页
 */
public interface StudentService {

    /**
     * 创建新学生
     *
     * 业务规则：
     * 1. 学号去除首尾空格后不能为空
     * 2. 学号必须全局唯一，重复时抛出异常
     * 3. 姓名、班级必填
     *
     * @param request 学生创建请求
     * @return 创建成功的学生实体（包含生成的ID和时间戳）
     * @throws IllegalArgumentException 当学号重复时抛出
     */
    Student createStudent(StudentCreateRequest request);

    /**
     * 根据ID查询学生
     *
     * @param id 学生ID
     * @return 学生实体，不存在时返回 null
     */
    Student getStudentById(Long id);
}

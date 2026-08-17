package com.ecommerce.studentscorebackend.service;

import com.ecommerce.studentscorebackend.dto.PageResponse;
import com.ecommerce.studentscorebackend.dto.StudentCreateRequest;
import com.ecommerce.studentscorebackend.dto.StudentQueryRequest;
import com.ecommerce.studentscorebackend.dto.StudentUpdateRequest;
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

    /**
     * 更新学生信息
     *
     * 业务规则：
     * 1. 学号不允许修改
     * 2. 姓名、班级必填
     * 3. 学生不存在时抛出异常
     *
     * @param id 学生ID
     * @param request 更新请求
     * @return 更新后的学生实体
     * @throws IllegalArgumentException 当学生不存在时抛出
     */
    Student updateStudent(Long id, StudentUpdateRequest request);

    /**
     * 删除学生
     *
     * 业务规则：
     * 1. 学生不存在时抛出异常
     * 2. 已有成绩时默认返回冲突错误
     * 3. 明确选择"同时删除成绩"后才在同一事务中级联删除
     *
     * @param id 学生ID
     * @param cascadeDelete 是否级联删除成绩（true=同时删除成绩，false=有成绩时拒绝删除）
     * @throws IllegalArgumentException 当学生不存在或有成绩冲突时抛出
     */
    void deleteStudent(Long id, boolean cascadeDelete);

    /**
     * 查询学生列表（支持搜索和分页）
     *
     * 查询条件：
     * 1. 学号：精确匹配或前缀匹配
     * 2. 姓名：包含匹配（模糊查询）
     * 3. 班级：精确匹配
     * 4. 分页：默认每页20条，允许10/20/50/100，最大100条
     *
     * @param request 查询请求
     * @return 分页结果
     */
    PageResponse<Student> queryStudents(StudentQueryRequest request);
}

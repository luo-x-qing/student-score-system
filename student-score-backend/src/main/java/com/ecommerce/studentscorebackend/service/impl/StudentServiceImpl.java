package com.ecommerce.studentscorebackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ecommerce.studentscorebackend.dto.StudentCreateRequest;
import com.ecommerce.studentscorebackend.entity.Student;
import com.ecommerce.studentscorebackend.mapper.StudentMapper;
import com.ecommerce.studentscorebackend.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 学生业务逻辑实现类
 *
 * 实现学生信息的管理，包括创建、查询、更新和删除
 * 使用 MyBatis-Plus 进行数据库操作
 */
@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentMapper studentMapper;

    /**
     * 创建新学生
     *
     * 实现步骤：
     * 1. 去除学号首尾空格
     * 2. 检查学号是否已存在（唯一性校验）
     * 3. 构建学生实体
     * 4. 插入数据库
     * 5. 返回包含ID和时间戳的完整实体
     *
     * @param request 学生创建请求
     * @return 创建成功的学生实体
     * @throws IllegalArgumentException 当学号重复时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Student createStudent(StudentCreateRequest request) {
        // 1. 去除学号首尾空格
        String studentNo = request.getStudentNo().trim();

        // 2. 检查学号唯一性
        LambdaQueryWrapper<Student> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Student::getStudentNo, studentNo);
        Long count = studentMapper.selectCount(queryWrapper);

        if (count > 0) {
            throw new IllegalArgumentException("学号已存在: " + studentNo);
        }

        // 3. 构建学生实体
        Student student = new Student();
        student.setStudentNo(studentNo);
        student.setName(request.getName());
        student.setGender(request.getGender());
        student.setClassName(request.getClassName());
        student.setRemarks(request.getRemarks());

        // 4. 插入数据库（ID和时间戳由数据库自动生成）
        studentMapper.insert(student);

        // 5. 返回完整实体（MyBatis-Plus 会自动填充生成的ID）
        return student;
    }

    /**
     * 根据ID查询学生
     *
     * @param id 学生ID
     * @return 学生实体，不存在时返回 null
     */
    @Override
    public Student getStudentById(Long id) {
        return studentMapper.selectById(id);
    }
}

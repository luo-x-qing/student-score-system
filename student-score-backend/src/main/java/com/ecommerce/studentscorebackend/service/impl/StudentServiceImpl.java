package com.ecommerce.studentscorebackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ecommerce.studentscorebackend.dto.PageResponse;
import com.ecommerce.studentscorebackend.dto.StudentCreateRequest;
import com.ecommerce.studentscorebackend.dto.StudentQueryRequest;
import com.ecommerce.studentscorebackend.dto.StudentUpdateRequest;
import com.ecommerce.studentscorebackend.entity.Student;
import com.ecommerce.studentscorebackend.mapper.StudentMapper;
import com.ecommerce.studentscorebackend.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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

    /**
     * 更新学生信息
     *
     * 实现步骤：
     * 1. 检查学生是否存在
     * 2. 更新学生信息（学号不允许修改）
     * 3. 返回更新后的完整实体
     *
     * @param id 学生ID
     * @param request 更新请求
     * @return 更新后的学生实体
     * @throws IllegalArgumentException 当学生不存在时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Student updateStudent(Long id, StudentUpdateRequest request) {
        // 1. 检查学生是否存在
        Student student = studentMapper.selectById(id);
        if (student == null) {
            throw new IllegalArgumentException("学生不存在，ID: " + id);
        }

        // 2. 更新学生信息（学号不允许修改）
        student.setName(request.getName());
        student.setGender(request.getGender());
        student.setClassName(request.getClassName());
        student.setRemarks(request.getRemarks());

        // 执行更新（updated_at 由数据库自动更新）
        studentMapper.updateById(student);

        // 3. 返回更新后的完整实体
        return student;
    }

    /**
     * 删除学生
     *
     * 实现步骤：
     * 1. 检查学生是否存在
     * 2. 检查是否有关联成绩
     * 3. 根据 cascadeDelete 参数决定是否级联删除
     * 4. 执行删除操作
     *
     * @param id 学生ID
     * @param cascadeDelete 是否级联删除成绩
     * @throws IllegalArgumentException 当学生不存在或有成绩冲突时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteStudent(Long id, boolean cascadeDelete) {
        // 1. 检查学生是否存在
        Student student = studentMapper.selectById(id);
        if (student == null) {
            throw new IllegalArgumentException("学生不存在，ID: " + id);
        }

        // 2. 检查是否有关联成绩（当前阶段暂不实现，待 Phase 12 实现成绩模块后补充）
        // TODO: Phase 12 - 检查 student_score 表是否有该学生的成绩记录
        // 如果有成绩且 cascadeDelete=false，则抛出异常
        // 如果 cascadeDelete=true，则同时删除成绩记录

        // 3. 执行删除操作
        studentMapper.deleteById(id);
    }

    /**
     * 查询学生列表（支持搜索和分页）
     *
     * 实现步骤：
     * 1. 构建查询条件（学号、姓名、班级）
     * 2. 先查询总记录数（不包含排序）
     * 3. 再查询当前页数据（包含排序和分页）
     * 4. 封装分页结果
     *
     * @param request 查询请求
     * @return 分页结果
     */
    @Override
    public PageResponse<Student> queryStudents(StudentQueryRequest request) {
        // 1. 构建查询条件（用于 count 和查询数据）
        LambdaQueryWrapper<Student> countWrapper = new LambdaQueryWrapper<>();

        // 学号查询：支持精确匹配或前缀匹配
        if (StringUtils.hasText(request.getStudentNo())) {
            countWrapper.like(Student::getStudentNo, request.getStudentNo());
        }

        // 姓名查询：包含匹配（模糊查询）
        if (StringUtils.hasText(request.getName())) {
            countWrapper.like(Student::getName, request.getName());
        }

        // 班级查询：精确匹配
        if (StringUtils.hasText(request.getClassName())) {
            countWrapper.eq(Student::getClassName, request.getClassName());
        }

        // 2. 查询总记录数（不包含排序，避免 H2 的 GROUP BY 错误）
        Long total = studentMapper.selectCount(countWrapper);

        // 3. 构建数据查询条件（包含排序和分页）
        LambdaQueryWrapper<Student> dataWrapper = new LambdaQueryWrapper<>();

        // 重新添加查询条件
        if (StringUtils.hasText(request.getStudentNo())) {
            dataWrapper.like(Student::getStudentNo, request.getStudentNo());
        }
        if (StringUtils.hasText(request.getName())) {
            dataWrapper.like(Student::getName, request.getName());
        }
        if (StringUtils.hasText(request.getClassName())) {
            dataWrapper.eq(Student::getClassName, request.getClassName());
        }

        // 按学号升序排序
        dataWrapper.orderByAsc(Student::getStudentNo);

        // 设置分页参数
        int page = request.getPage() != null ? request.getPage() : 1;
        int pageSize = request.getPageSize() != null ? request.getPageSize() : 20;

        // 限制最大每页记录数为100
        if (pageSize > 100) {
            pageSize = 100;
        }

        // 计算偏移量
        int offset = (page - 1) * pageSize;

        // 使用 LIMIT 和 OFFSET 进行分页查询
        dataWrapper.last("LIMIT " + pageSize + " OFFSET " + offset);

        // 查询当前页数据
        java.util.List<Student> records = studentMapper.selectList(dataWrapper);

        // 4. 封装分页结果
        return new PageResponse<>(records, total, page, pageSize);
    }
}

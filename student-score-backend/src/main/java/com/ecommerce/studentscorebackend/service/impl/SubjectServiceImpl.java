package com.ecommerce.studentscorebackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ecommerce.studentscorebackend.dto.SubjectCreateRequest;
import com.ecommerce.studentscorebackend.dto.SubjectUpdateRequest;
import com.ecommerce.studentscorebackend.entity.Subject;
import com.ecommerce.studentscorebackend.mapper.SubjectMapper;
import com.ecommerce.studentscorebackend.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 学科业务逻辑实现类
 *
 * 实现学科信息的管理，包括创建、查询、更新和删除
 * 使用 MyBatis-Plus 进行数据库操作
 */
@Service
public class SubjectServiceImpl implements SubjectService {

    @Autowired
    private SubjectMapper subjectMapper;

    /**
     * 创建新学科
     *
     * 实现步骤：
     * 1. 检查学科名称是否已存在（唯一性校验）
     * 2. 校验权重为正数（由 DTO 的 @Positive 注解保证）
     * 3. 构建学科实体
     * 4. 插入数据库
     * 5. 返回包含ID和时间戳的完整实体
     *
     * @param request 学科创建请求
     * @return 创建成功的学科实体
     * @throws IllegalArgumentException 当学科名称重复时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Subject createSubject(SubjectCreateRequest request) {
        // 1. 检查学科名称唯一性
        LambdaQueryWrapper<Subject> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Subject::getSubjectName, request.getSubjectName());
        Long count = subjectMapper.selectCount(queryWrapper);

        if (count > 0) {
            throw new IllegalArgumentException("学科名称已存在: " + request.getSubjectName());
        }

        // 2. 构建学科实体
        Subject subject = new Subject();
        subject.setSubjectName(request.getSubjectName());
        subject.setWeightRate(request.getWeightRate());

        // 3. 插入数据库（ID和时间戳由数据库自动生成）
        subjectMapper.insert(subject);

        // 4. 返回完整实体（MyBatis-Plus 会自动填充生成的ID）
        return subject;
    }

    /**
     * 根据ID查询学科
     *
     * @param id 学科ID
     * @return 学科实体，不存在时返回 null
     */
    @Override
    public Subject getSubjectById(Long id) {
        return subjectMapper.selectById(id);
    }

    /**
     * 更新学科信息
     *
     * 实现步骤：
     * 1. 检查学科是否存在
     * 2. 检查学科名称是否与其他学科重复
     * 3. 更新学科信息
     * 4. 返回更新后的完整实体
     *
     * @param id 学科ID
     * @param request 更新请求
     * @return 更新后的学科实体
     * @throws IllegalArgumentException 当学科不存在或名称重复时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Subject updateSubject(Long id, SubjectUpdateRequest request) {
        // 1. 检查学科是否存在
        Subject subject = subjectMapper.selectById(id);
        if (subject == null) {
            throw new IllegalArgumentException("学科不存在，ID: " + id);
        }

        // 2. 检查学科名称是否与其他学科重复
        LambdaQueryWrapper<Subject> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Subject::getSubjectName, request.getSubjectName());
        queryWrapper.ne(Subject::getId, id); // 排除当前学科
        Long count = subjectMapper.selectCount(queryWrapper);

        if (count > 0) {
            throw new IllegalArgumentException("学科名称已存在: " + request.getSubjectName());
        }

        // 3. 更新学科信息
        subject.setSubjectName(request.getSubjectName());
        subject.setWeightRate(request.getWeightRate());

        // 执行更新（updated_at 由数据库自动更新）
        subjectMapper.updateById(subject);

        // 4. 返回更新后的完整实体
        return subject;
    }

    /**
     * 删除学科
     *
     * 实现步骤：
     * 1. 检查学科是否存在
     * 2. 检查是否有成绩引用（引用保护）
     * 3. 执行删除操作
     *
     * @param id 学科ID
     * @throws IllegalArgumentException 当学科不存在或有成绩引用时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSubject(Long id) {
        // 1. 检查学科是否存在
        Subject subject = subjectMapper.selectById(id);
        if (subject == null) {
            throw new IllegalArgumentException("学科不存在，ID: " + id);
        }

        // 2. 检查是否有成绩引用（当前阶段暂不实现，待 Phase 12 实现成绩模块后补充）
        // TODO: Phase 12 - 检查 student_score 表是否有该学科的成绩记录
        // 如果有成绩引用，则抛出异常：throw new IllegalArgumentException("学科已被成绩引用，无法删除");

        // 3. 执行删除操作
        subjectMapper.deleteById(id);
    }

    /**
     * 查询所有学科列表
     *
     * 按学科名称升序排序
     *
     * @return 学科列表
     */
    @Override
    public List<Subject> getAllSubjects() {
        LambdaQueryWrapper<Subject> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Subject::getSubjectName);
        return subjectMapper.selectList(queryWrapper);
    }
}

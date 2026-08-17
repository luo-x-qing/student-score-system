package com.ecommerce.studentscorebackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ecommerce.studentscorebackend.dto.ExamTypeCreateRequest;
import com.ecommerce.studentscorebackend.dto.ExamTypeUpdateRequest;
import com.ecommerce.studentscorebackend.entity.ExamType;
import com.ecommerce.studentscorebackend.mapper.ExamTypeMapper;
import com.ecommerce.studentscorebackend.service.ExamTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 考试类型业务逻辑实现类
 *
 * 实现考试类型信息的管理，包括创建、查询、更新和删除
 * 使用 MyBatis-Plus 进行数据库操作
 */
@Service
public class ExamTypeServiceImpl implements ExamTypeService {

    @Autowired
    private ExamTypeMapper examTypeMapper;

    /**
     * 创建新考试类型
     *
     * 实现步骤：
     * 1. 检查考试类型名称是否已存在（唯一性校验）
     * 2. 校验比率范围（由 DTO 的 @DecimalMin/@DecimalMax 注解保证）
     * 3. 构建考试类型实体
     * 4. 插入数据库
     * 5. 返回包含ID和时间戳的完整实体
     *
     * @param request 考试类型创建请求
     * @return 创建成功的考试类型实体
     * @throws IllegalArgumentException 当考试类型名称重复时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExamType createExamType(ExamTypeCreateRequest request) {
        // 1. 检查考试类型名称唯一性
        LambdaQueryWrapper<ExamType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExamType::getTypeName, request.getTypeName());
        Long count = examTypeMapper.selectCount(queryWrapper);

        if (count > 0) {
            throw new IllegalArgumentException("考试类型名称已存在: " + request.getTypeName());
        }

        // 2. 构建考试类型实体
        ExamType examType = new ExamType();
        examType.setTypeName(request.getTypeName());
        examType.setRate(request.getRate());

        // 3. 插入数据库（ID和时间戳由数据库自动生成）
        examTypeMapper.insert(examType);

        // 4. 返回完整实体（MyBatis-Plus 会自动填充生成的ID）
        return examType;
    }

    /**
     * 根据ID查询考试类型
     *
     * @param id 考试类型ID
     * @return 考试类型实体，不存在时返回 null
     */
    @Override
    public ExamType getExamTypeById(Long id) {
        return examTypeMapper.selectById(id);
    }

    /**
     * 更新考试类型信息
     *
     * 实现步骤：
     * 1. 检查考试类型是否存在
     * 2. 检查考试类型名称是否与其他考试类型重复
     * 3. 更新考试类型信息
     * 4. 返回更新后的完整实体
     *
     * @param id 考试类型ID
     * @param request 更新请求
     * @return 更新后的考试类型实体
     * @throws IllegalArgumentException 当考试类型不存在或名称重复时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExamType updateExamType(Long id, ExamTypeUpdateRequest request) {
        // 1. 检查考试类型是否存在
        ExamType examType = examTypeMapper.selectById(id);
        if (examType == null) {
            throw new IllegalArgumentException("考试类型不存在，ID: " + id);
        }

        // 2. 检查考试类型名称是否与其他考试类型重复
        LambdaQueryWrapper<ExamType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExamType::getTypeName, request.getTypeName());
        queryWrapper.ne(ExamType::getId, id); // 排除当前考试类型
        Long count = examTypeMapper.selectCount(queryWrapper);

        if (count > 0) {
            throw new IllegalArgumentException("考试类型名称已存在: " + request.getTypeName());
        }

        // 3. 更新考试类型信息
        examType.setTypeName(request.getTypeName());
        examType.setRate(request.getRate());

        // 执行更新（updated_at 由数据库自动更新）
        examTypeMapper.updateById(examType);

        // 4. 返回更新后的完整实体
        return examType;
    }

    /**
     * 删除考试类型
     *
     * 实现步骤：
     * 1. 检查考试类型是否存在
     * 2. 检查是否有成绩引用（引用保护）
     * 3. 执行删除操作
     *
     * @param id 考试类型ID
     * @throws IllegalArgumentException 当考试类型不存在或有成绩引用时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteExamType(Long id) {
        // 1. 检查考试类型是否存在
        ExamType examType = examTypeMapper.selectById(id);
        if (examType == null) {
            throw new IllegalArgumentException("考试类型不存在，ID: " + id);
        }

        // 2. 检查是否有成绩引用（当前阶段暂不实现，待 Phase 12 实现成绩模块后补充）
        // TODO: Phase 12 - 检查 student_score 表是否有该考试类型的成绩记录
        // 如果有成绩引用，则抛出异常：throw new IllegalArgumentException("考试类型已被成绩引用，无法删除");

        // 3. 执行删除操作
        examTypeMapper.deleteById(id);
    }

    /**
     * 查询所有考试类型列表
     *
     * 按考试类型名称升序排序
     *
     * @return 考试类型列表
     */
    @Override
    public List<ExamType> getAllExamTypes() {
        LambdaQueryWrapper<ExamType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(ExamType::getTypeName);
        return examTypeMapper.selectList(queryWrapper);
    }
}

package com.ecommerce.studentscorebackend.service;

import com.ecommerce.studentscorebackend.dto.ExamTypeCreateRequest;
import com.ecommerce.studentscorebackend.dto.ExamTypeUpdateRequest;
import com.ecommerce.studentscorebackend.entity.ExamType;

import java.util.List;

/**
 * 考试类型业务逻辑接口
 *
 * 职责：
 * 1. 考试类型信息的增删改查
 * 2. 考试类型名称唯一性校验
 * 3. 比率范围校验（0-100）
 * 4. 删除前成绩引用检查
 */
public interface ExamTypeService {

    /**
     * 创建新考试类型
     *
     * 业务规则：
     * 1. 考试类型名称必须全局唯一，重复时抛出异常
     * 2. 比率必须在 0-100 之间
     *
     * @param request 考试类型创建请求
     * @return 创建成功的考试类型实体（包含生成的ID和时间戳）
     * @throws IllegalArgumentException 当考试类型名称重复或比率无效时抛出
     */
    ExamType createExamType(ExamTypeCreateRequest request);

    /**
     * 根据ID查询考试类型
     *
     * @param id 考试类型ID
     * @return 考试类型实体，不存在时返回 null
     */
    ExamType getExamTypeById(Long id);

    /**
     * 更新考试类型信息
     *
     * 业务规则：
     * 1. 考试类型不存在时抛出异常
     * 2. 考试类型名称不能与其他考试类型重复
     * 3. 比率必须在 0-100 之间
     *
     * @param id 考试类型ID
     * @param request 更新请求
     * @return 更新后的考试类型实体
     * @throws IllegalArgumentException 当考试类型不存在或名称重复时抛出
     */
    ExamType updateExamType(Long id, ExamTypeUpdateRequest request);

    /**
     * 删除考试类型
     *
     * 业务规则：
     * 1. 考试类型不存在时抛出异常
     * 2. 已被成绩引用的考试类型不允许删除（引用保护）
     *
     * @param id 考试类型ID
     * @throws IllegalArgumentException 当考试类型不存在或有成绩引用时抛出
     */
    void deleteExamType(Long id);

    /**
     * 查询所有考试类型列表
     *
     * 按考试类型名称升序排序
     *
     * @return 考试类型列表
     */
    List<ExamType> getAllExamTypes();
}

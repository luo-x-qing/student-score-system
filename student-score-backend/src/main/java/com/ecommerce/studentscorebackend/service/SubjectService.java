package com.ecommerce.studentscorebackend.service;

import com.ecommerce.studentscorebackend.dto.SubjectCreateRequest;
import com.ecommerce.studentscorebackend.dto.SubjectUpdateRequest;
import com.ecommerce.studentscorebackend.entity.Subject;

import java.util.List;

/**
 * 学科业务逻辑接口
 *
 * 职责：
 * 1. 学科信息的增删改查
 * 2. 学科名称唯一性校验
 * 3. 权重正数校验
 * 4. 删除前成绩引用检查
 */
public interface SubjectService {

    /**
     * 创建新学科
     *
     * 业务规则：
     * 1. 学科名称必须全局唯一，重复时抛出异常
     * 2. 权重必须为正数（> 0）
     *
     * @param request 学科创建请求
     * @return 创建成功的学科实体（包含生成的ID和时间戳）
     * @throws IllegalArgumentException 当学科名称重复或权重无效时抛出
     */
    Subject createSubject(SubjectCreateRequest request);

    /**
     * 根据ID查询学科
     *
     * @param id 学科ID
     * @return 学科实体，不存在时返回 null
     */
    Subject getSubjectById(Long id);

    /**
     * 更新学科信息
     *
     * 业务规则：
     * 1. 学科不存在时抛出异常
     * 2. 学科名称不能与其他学科重复
     * 3. 权重必须为正数
     *
     * @param id 学科ID
     * @param request 更新请求
     * @return 更新后的学科实体
     * @throws IllegalArgumentException 当学科不存在或名称重复时抛出
     */
    Subject updateSubject(Long id, SubjectUpdateRequest request);

    /**
     * 删除学科
     *
     * 业务规则：
     * 1. 学科不存在时抛出异常
     * 2. 已被成绩引用的学科不允许删除（引用保护）
     *
     * @param id 学科ID
     * @throws IllegalArgumentException 当学科不存在或有成绩引用时抛出
     */
    void deleteSubject(Long id);

    /**
     * 查询所有学科列表
     *
     * 按学科名称升序排序
     *
     * @return 学科列表
     */
    List<Subject> getAllSubjects();
}

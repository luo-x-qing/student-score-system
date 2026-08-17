package com.ecommerce.studentscorebackend.service;

import com.ecommerce.studentscorebackend.dto.ScoreBatchSaveRequest;
import com.ecommerce.studentscorebackend.dto.ScoreSaveRequest;
import com.ecommerce.studentscorebackend.entity.StudentScore;

import java.util.List;

/**
 * 学生成绩业务逻辑接口
 *
 * 职责：
 * 1. 成绩的保存（单条/批量）、修改、删除
 * 2. 唯一性校验（学生ID + 学科ID + 考试类型ID 组合唯一）
 * 3. 成绩范围校验（0-100 或 null）
 * 4. 外键存在性校验（学生、学科、考试类型必须存在）
 */
public interface StudentScoreService {

    /**
     * 保存单条成绩
     *
     * 业务规则：
     * 1. 学生、学科、考试类型必须存在
     * 2. 学生ID + 学科ID + 考试类型ID 组合唯一
     * 3. 如果已存在相同组合的成绩，则更新；否则新增
     * 4. 成绩范围：0-100 或 null
     *
     * @param request 成绩保存请求
     * @return 保存后的成绩实体
     * @throws IllegalArgumentException 当学生、学科或考试类型不存在时抛出
     */
    StudentScore saveScore(ScoreSaveRequest request);

    /**
     * 批量保存成绩
     *
     * 业务规则：
     * 1. 所有成绩必须通过校验（外键存在性、范围校验）
     * 2. 使用事务保证整批保存的原子性（全部成功或全部失败）
     * 3. 如果已存在相同组合的成绩，则更新；否则新增
     *
     * @param request 批量成绩保存请求
     * @return 保存后的成绩列表
     * @throws IllegalArgumentException 当任何一条成绩校验失败时，整批回滚
     */
    List<StudentScore> batchSaveScores(ScoreBatchSaveRequest request);

    /**
     * 根据ID删除成绩
     *
     * @param id 成绩ID
     * @throws IllegalArgumentException 当成绩不存在时抛出
     */
    void deleteScore(Long id);

    /**
     * 根据ID查询成绩
     *
     * @param id 成绩ID
     * @return 成绩实体，不存在时返回 null
     */
    StudentScore getScoreById(Long id);

    /**
     * 查询指定学生的所有成绩
     *
     * @param studentId 学生ID
     * @return 成绩列表
     */
    List<StudentScore> getScoresByStudentId(Long studentId);

    /**
     * 查询指定学科的所有成绩
     *
     * @param subjectId 学科ID
     * @return 成绩列表
     */
    List<StudentScore> getScoresBySubjectId(Long subjectId);

    /**
     * 查询指定考试类型的所有成绩
     *
     * @param examTypeId 考试类型ID
     * @return 成绩列表
     */
    List<StudentScore> getScoresByExamTypeId(Long examTypeId);
}

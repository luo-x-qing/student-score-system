package com.ecommerce.studentscorebackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ecommerce.studentscorebackend.dto.ScoreBatchSaveRequest;
import com.ecommerce.studentscorebackend.dto.ScoreSaveRequest;
import com.ecommerce.studentscorebackend.entity.ExamType;
import com.ecommerce.studentscorebackend.entity.Student;
import com.ecommerce.studentscorebackend.entity.StudentScore;
import com.ecommerce.studentscorebackend.entity.Subject;
import com.ecommerce.studentscorebackend.mapper.ExamTypeMapper;
import com.ecommerce.studentscorebackend.mapper.StudentMapper;
import com.ecommerce.studentscorebackend.mapper.StudentScoreMapper;
import com.ecommerce.studentscorebackend.mapper.SubjectMapper;
import com.ecommerce.studentscorebackend.service.StudentScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 学生成绩业务逻辑实现类
 *
 * 实现成绩信息的管理，包括保存、查询和删除
 * 使用 MyBatis-Plus 进行数据库操作
 */
@Service
public class StudentScoreServiceImpl implements StudentScoreService {

    @Autowired
    private StudentScoreMapper studentScoreMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private SubjectMapper subjectMapper;

    @Autowired
    private ExamTypeMapper examTypeMapper;

    /**
     * 保存单条成绩
     *
     * 实现步骤：
     * 1. 校验学生、学科、考试类型是否存在
     * 2. 检查是否已存在相同组合的成绩
     * 3. 如果存在则更新，否则新增
     * 4. 返回保存后的完整实体
     *
     * @param request 成绩保存请求
     * @return 保存后的成绩实体
     * @throws IllegalArgumentException 当学生、学科或考试类型不存在时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public StudentScore saveScore(ScoreSaveRequest request) {
        // 1. 校验外键存在性
        validateReferences(request.getStudentId(), request.getSubjectId(), request.getExamTypeId());

        // 2. 检查是否已存在相同组合的成绩
        LambdaQueryWrapper<StudentScore> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StudentScore::getStudentId, request.getStudentId());
        queryWrapper.eq(StudentScore::getSubjectId, request.getSubjectId());
        queryWrapper.eq(StudentScore::getExamTypeId, request.getExamTypeId());
        StudentScore existingScore = studentScoreMapper.selectOne(queryWrapper);

        if (existingScore != null) {
            // 3a. 更新已有成绩
            existingScore.setScore(request.getScore());
            studentScoreMapper.updateById(existingScore);
            return existingScore;
        } else {
            // 3b. 新增成绩
            StudentScore newScore = new StudentScore();
            newScore.setStudentId(request.getStudentId());
            newScore.setSubjectId(request.getSubjectId());
            newScore.setExamTypeId(request.getExamTypeId());
            newScore.setScore(request.getScore());
            studentScoreMapper.insert(newScore);
            return newScore;
        }
    }

    /**
     * 批量保存成绩
     *
     * 实现步骤：
     * 1. 使用事务保证整批操作的原子性
     * 2. 逐条保存成绩（复用 saveScore 方法）
     * 3. 如果任何一条失败，整批回滚
     * 4. 返回保存后的成绩列表
     *
     * @param request 批量成绩保存请求
     * @return 保存后的成绩列表
     * @throws IllegalArgumentException 当任何一条成绩校验失败时，整批回滚
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<StudentScore> batchSaveScores(ScoreBatchSaveRequest request) {
        List<StudentScore> savedScores = new ArrayList<>();

        // 逐条保存成绩（在同一个事务中）
        for (ScoreSaveRequest scoreRequest : request.getScores()) {
            StudentScore savedScore = saveScore(scoreRequest);
            savedScores.add(savedScore);
        }

        return savedScores;
    }

    /**
     * 根据ID删除成绩
     *
     * @param id 成绩ID
     * @throws IllegalArgumentException 当成绩不存在时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteScore(Long id) {
        StudentScore score = studentScoreMapper.selectById(id);
        if (score == null) {
            throw new IllegalArgumentException("成绩不存在，ID: " + id);
        }
        studentScoreMapper.deleteById(id);
    }

    /**
     * 根据ID查询成绩
     *
     * @param id 成绩ID
     * @return 成绩实体，不存在时返回 null
     */
    @Override
    public StudentScore getScoreById(Long id) {
        return studentScoreMapper.selectById(id);
    }

    /**
     * 查询指定学生的所有成绩
     *
     * @param studentId 学生ID
     * @return 成绩列表
     */
    @Override
    public List<StudentScore> getScoresByStudentId(Long studentId) {
        LambdaQueryWrapper<StudentScore> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StudentScore::getStudentId, studentId);
        return studentScoreMapper.selectList(queryWrapper);
    }

    /**
     * 查询指定学科的所有成绩
     *
     * @param subjectId 学科ID
     * @return 成绩列表
     */
    @Override
    public List<StudentScore> getScoresBySubjectId(Long subjectId) {
        LambdaQueryWrapper<StudentScore> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StudentScore::getSubjectId, subjectId);
        return studentScoreMapper.selectList(queryWrapper);
    }

    /**
     * 查询指定考试类型的所有成绩
     *
     * @param examTypeId 考试类型ID
     * @return 成绩列表
     */
    @Override
    public List<StudentScore> getScoresByExamTypeId(Long examTypeId) {
        LambdaQueryWrapper<StudentScore> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StudentScore::getExamTypeId, examTypeId);
        return studentScoreMapper.selectList(queryWrapper);
    }

    /**
     * 校验外键引用是否存在
     *
     * @param studentId 学生ID
     * @param subjectId 学科ID
     * @param examTypeId 考试类型ID
     * @throws IllegalArgumentException 当学生、学科或考试类型不存在时抛出
     */
    private void validateReferences(Long studentId, Long subjectId, Long examTypeId) {
        // 校验学生是否存在
        Student student = studentMapper.selectById(studentId);
        if (student == null) {
            throw new IllegalArgumentException("学生不存在，ID: " + studentId);
        }

        // 校验学科是否存在
        Subject subject = subjectMapper.selectById(subjectId);
        if (subject == null) {
            throw new IllegalArgumentException("学科不存在，ID: " + subjectId);
        }

        // 校验考试类型是否存在
        ExamType examType = examTypeMapper.selectById(examTypeId);
        if (examType == null) {
            throw new IllegalArgumentException("考试类型不存在，ID: " + examTypeId);
        }
    }
}

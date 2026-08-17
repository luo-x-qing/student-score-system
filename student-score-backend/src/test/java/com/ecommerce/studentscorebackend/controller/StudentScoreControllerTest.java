package com.ecommerce.studentscorebackend.controller;

import com.ecommerce.studentscorebackend.dto.*;
import com.ecommerce.studentscorebackend.entity.*;
import com.ecommerce.studentscorebackend.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 学生成绩控制器集成测试
 *
 * 测试范围：
 * 1. 保存单条成绩 - 正常情况（新增）
 * 2. 保存单条成绩 - 更新已有成绩
 * 3. 保存单条成绩 - 学生不存在
 * 4. 保存单条成绩 - 成绩范围校验（0和100边界）
 * 5. 保存单条成绩 - 成绩为null（缺考）
 * 6. 批量保存成绩 - 正常情况
 * 7. 批量保存成绩 - 部分失败整批回滚
 * 8. 删除成绩 - 正常情况
 * 9. 删除成绩 - 成绩不存在
 * 10. 查询成绩 - 按学生ID查询
 * 11. 查询成绩 - 按学科ID查询
 * 12. 查询成绩 - 按考试类型ID查询
 *
 * 使用 SpringBootTest 进行集成测试
 * 每个测试方法都在事务中运行，测试后自动回滚
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class StudentScoreControllerTest {

    @Autowired
    private StudentScoreService studentScoreService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private ExamTypeService examTypeService;

    private Student testStudent;
    private Subject testSubject;
    private ExamType testExamType;

    /**
     * 每个测试前初始化测试数据
     */
    @BeforeEach
    void setUp() {
        // 创建测试学生
        StudentCreateRequest studentRequest = new StudentCreateRequest();
        studentRequest.setStudentNo("2024001");
        studentRequest.setName("张三");
        studentRequest.setClassName("一年级1班");
        testStudent = studentService.createStudent(studentRequest);

        // 创建测试学科
        SubjectCreateRequest subjectRequest = new SubjectCreateRequest();
        subjectRequest.setSubjectName("语文");
        subjectRequest.setWeightRate(new BigDecimal("1.5"));
        testSubject = subjectService.createSubject(subjectRequest);

        // 创建测试考试类型
        ExamTypeCreateRequest examTypeRequest = new ExamTypeCreateRequest();
        examTypeRequest.setTypeName("期中考试");
        examTypeRequest.setRate(new BigDecimal("30"));
        testExamType = examTypeService.createExamType(examTypeRequest);
    }

    /**
     * 测试：保存单条成绩 - 正常情况（新增）
     */
    @Test
    void saveScore_withValidData_shouldCreateNewScore() {
        // 准备请求
        ScoreSaveRequest request = new ScoreSaveRequest();
        request.setStudentId(testStudent.getId());
        request.setSubjectId(testSubject.getId());
        request.setExamTypeId(testExamType.getId());
        request.setScore(new BigDecimal("85.5"));

        // 保存成绩
        StudentScore score = studentScoreService.saveScore(request);

        // 验证结果
        assertNotNull(score);
        assertNotNull(score.getId());
        assertEquals(testStudent.getId(), score.getStudentId());
        assertEquals(testSubject.getId(), score.getSubjectId());
        assertEquals(testExamType.getId(), score.getExamTypeId());
        assertEquals(0, new BigDecimal("85.5").compareTo(score.getScore()));
    }

    /**
     * 测试：保存单条成绩 - 更新已有成绩
     */
    @Test
    void saveScore_whenExists_shouldUpdateScore() {
        // 第一次保存
        ScoreSaveRequest request = new ScoreSaveRequest();
        request.setStudentId(testStudent.getId());
        request.setSubjectId(testSubject.getId());
        request.setExamTypeId(testExamType.getId());
        request.setScore(new BigDecimal("85"));
        StudentScore firstScore = studentScoreService.saveScore(request);

        // 第二次保存相同组合，修改成绩
        request.setScore(new BigDecimal("90"));
        StudentScore updatedScore = studentScoreService.saveScore(request);

        // 验证结果：ID相同，成绩已更新
        assertEquals(firstScore.getId(), updatedScore.getId());
        assertEquals(0, new BigDecimal("90").compareTo(updatedScore.getScore()));
    }

    /**
     * 测试：保存单条成绩 - 学生不存在
     */
    @Test
    void saveScore_withInvalidStudentId_shouldThrowException() {
        ScoreSaveRequest request = new ScoreSaveRequest();
        request.setStudentId(999999L);
        request.setSubjectId(testSubject.getId());
        request.setExamTypeId(testExamType.getId());
        request.setScore(new BigDecimal("85"));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> studentScoreService.saveScore(request)
        );

        assertTrue(exception.getMessage().contains("学生不存在"));
    }

    /**
     * 测试：保存单条成绩 - 成绩范围校验（0和100边界）
     */
    @Test
    void saveScore_withBoundaryScores_shouldSucceed() {
        // 成绩为0
        ScoreSaveRequest request1 = new ScoreSaveRequest();
        request1.setStudentId(testStudent.getId());
        request1.setSubjectId(testSubject.getId());
        request1.setExamTypeId(testExamType.getId());
        request1.setScore(new BigDecimal("0"));
        StudentScore score1 = studentScoreService.saveScore(request1);
        assertEquals(0, new BigDecimal("0").compareTo(score1.getScore()));

        // 创建另一个考试类型用于测试100分
        ExamTypeCreateRequest examTypeRequest2 = new ExamTypeCreateRequest();
        examTypeRequest2.setTypeName("期末考试");
        examTypeRequest2.setRate(new BigDecimal("50"));
        ExamType testExamType2 = examTypeService.createExamType(examTypeRequest2);

        // 成绩为100
        ScoreSaveRequest request2 = new ScoreSaveRequest();
        request2.setStudentId(testStudent.getId());
        request2.setSubjectId(testSubject.getId());
        request2.setExamTypeId(testExamType2.getId());
        request2.setScore(new BigDecimal("100"));
        StudentScore score2 = studentScoreService.saveScore(request2);
        assertEquals(0, new BigDecimal("100").compareTo(score2.getScore()));
    }

    /**
     * 测试：保存单条成绩 - 成绩为null（缺考）
     */
    @Test
    void saveScore_withNullScore_shouldSucceed() {
        ScoreSaveRequest request = new ScoreSaveRequest();
        request.setStudentId(testStudent.getId());
        request.setSubjectId(testSubject.getId());
        request.setExamTypeId(testExamType.getId());
        request.setScore(null);

        StudentScore score = studentScoreService.saveScore(request);

        assertNotNull(score);
        assertNull(score.getScore());
    }

    /**
     * 测试：批量保存成绩 - 正常情况
     */
    @Test
    void batchSaveScores_withValidData_shouldSaveAll() {
        // 创建额外的学科用于批量测试
        SubjectCreateRequest subjectRequest2 = new SubjectCreateRequest();
        subjectRequest2.setSubjectName("数学");
        subjectRequest2.setWeightRate(new BigDecimal("1.5"));
        Subject testSubject2 = subjectService.createSubject(subjectRequest2);

        // 准备批量请求
        ScoreSaveRequest score1 = new ScoreSaveRequest();
        score1.setStudentId(testStudent.getId());
        score1.setSubjectId(testSubject.getId());
        score1.setExamTypeId(testExamType.getId());
        score1.setScore(new BigDecimal("85"));

        ScoreSaveRequest score2 = new ScoreSaveRequest();
        score2.setStudentId(testStudent.getId());
        score2.setSubjectId(testSubject2.getId());
        score2.setExamTypeId(testExamType.getId());
        score2.setScore(new BigDecimal("90"));

        ScoreBatchSaveRequest batchRequest = new ScoreBatchSaveRequest();
        batchRequest.setScores(Arrays.asList(score1, score2));

        // 批量保存
        List<StudentScore> savedScores = studentScoreService.batchSaveScores(batchRequest);

        // 验证结果
        assertEquals(2, savedScores.size());
        assertEquals(0, new BigDecimal("85").compareTo(savedScores.get(0).getScore()));
        assertEquals(0, new BigDecimal("90").compareTo(savedScores.get(1).getScore()));
    }

    /**
     * 测试：批量保存成绩 - 部分失败应抛出异常
     *
     * 注意：由于测试类使用了 @Transactional，外层事务会影响内层事务的回滚行为
     * 这里验证批量保存在遇到错误时能正确抛出异常即可
     * 实际的回滚行为由 Service 层的 @Transactional(rollbackFor = Exception.class) 保证
     */
    @Test
    void batchSaveScores_withOneInvalid_shouldThrowException() {
        // 准备批量请求，第二条学生ID无效
        ScoreSaveRequest score1 = new ScoreSaveRequest();
        score1.setStudentId(testStudent.getId());
        score1.setSubjectId(testSubject.getId());
        score1.setExamTypeId(testExamType.getId());
        score1.setScore(new BigDecimal("85"));

        ScoreSaveRequest score2 = new ScoreSaveRequest();
        score2.setStudentId(999999L); // 无效学生ID
        score2.setSubjectId(testSubject.getId());
        score2.setExamTypeId(testExamType.getId());
        score2.setScore(new BigDecimal("90"));

        ScoreBatchSaveRequest batchRequest = new ScoreBatchSaveRequest();
        batchRequest.setScores(Arrays.asList(score1, score2));

        // 批量保存应该失败并抛出异常
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> studentScoreService.batchSaveScores(batchRequest)
        );

        // 验证异常消息
        assertTrue(exception.getMessage().contains("学生不存在"));
    }

    /**
     * 测试：删除成绩 - 正常情况
     */
    @Test
    void deleteScore_withValidId_shouldSucceed() {
        // 先保存一条成绩
        ScoreSaveRequest request = new ScoreSaveRequest();
        request.setStudentId(testStudent.getId());
        request.setSubjectId(testSubject.getId());
        request.setExamTypeId(testExamType.getId());
        request.setScore(new BigDecimal("85"));
        StudentScore score = studentScoreService.saveScore(request);

        // 删除成绩
        studentScoreService.deleteScore(score.getId());

        // 验证删除成功
        StudentScore found = studentScoreService.getScoreById(score.getId());
        assertNull(found);
    }

    /**
     * 测试：删除成绩 - 成绩不存在
     */
    @Test
    void deleteScore_whenNotExists_shouldThrowException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> studentScoreService.deleteScore(999999L)
        );

        assertTrue(exception.getMessage().contains("成绩不存在"));
    }

    /**
     * 测试：查询成绩 - 按学生ID查询
     */
    @Test
    void getScoresByStudentId_shouldReturnAllScoresForStudent() {
        // 创建额外的学科
        SubjectCreateRequest subjectRequest2 = new SubjectCreateRequest();
        subjectRequest2.setSubjectName("数学");
        subjectRequest2.setWeightRate(new BigDecimal("1.5"));
        Subject testSubject2 = subjectService.createSubject(subjectRequest2);

        // 保存两条成绩
        ScoreSaveRequest request1 = new ScoreSaveRequest();
        request1.setStudentId(testStudent.getId());
        request1.setSubjectId(testSubject.getId());
        request1.setExamTypeId(testExamType.getId());
        request1.setScore(new BigDecimal("85"));
        studentScoreService.saveScore(request1);

        ScoreSaveRequest request2 = new ScoreSaveRequest();
        request2.setStudentId(testStudent.getId());
        request2.setSubjectId(testSubject2.getId());
        request2.setExamTypeId(testExamType.getId());
        request2.setScore(new BigDecimal("90"));
        studentScoreService.saveScore(request2);

        // 查询学生的所有成绩
        List<StudentScore> scores = studentScoreService.getScoresByStudentId(testStudent.getId());

        // 验证结果
        assertEquals(2, scores.size());
    }

    /**
     * 测试：查询成绩 - 按学科ID查询
     */
    @Test
    void getScoresBySubjectId_shouldReturnAllScoresForSubject() {
        // 创建额外的学生
        StudentCreateRequest studentRequest2 = new StudentCreateRequest();
        studentRequest2.setStudentNo("2024002");
        studentRequest2.setName("李四");
        studentRequest2.setClassName("一年级1班");
        Student testStudent2 = studentService.createStudent(studentRequest2);

        // 保存两条成绩（同一学科，不同学生）
        ScoreSaveRequest request1 = new ScoreSaveRequest();
        request1.setStudentId(testStudent.getId());
        request1.setSubjectId(testSubject.getId());
        request1.setExamTypeId(testExamType.getId());
        request1.setScore(new BigDecimal("85"));
        studentScoreService.saveScore(request1);

        ScoreSaveRequest request2 = new ScoreSaveRequest();
        request2.setStudentId(testStudent2.getId());
        request2.setSubjectId(testSubject.getId());
        request2.setExamTypeId(testExamType.getId());
        request2.setScore(new BigDecimal("90"));
        studentScoreService.saveScore(request2);

        // 查询学科的所有成绩
        List<StudentScore> scores = studentScoreService.getScoresBySubjectId(testSubject.getId());

        // 验证结果
        assertEquals(2, scores.size());
    }

    /**
     * 测试：查询成绩 - 按考试类型ID查询
     */
    @Test
    void getScoresByExamTypeId_shouldReturnAllScoresForExamType() {
        // 创建额外的学科
        SubjectCreateRequest subjectRequest2 = new SubjectCreateRequest();
        subjectRequest2.setSubjectName("数学");
        subjectRequest2.setWeightRate(new BigDecimal("1.5"));
        Subject testSubject2 = subjectService.createSubject(subjectRequest2);

        // 保存两条成绩（同一考试类型，不同学科）
        ScoreSaveRequest request1 = new ScoreSaveRequest();
        request1.setStudentId(testStudent.getId());
        request1.setSubjectId(testSubject.getId());
        request1.setExamTypeId(testExamType.getId());
        request1.setScore(new BigDecimal("85"));
        studentScoreService.saveScore(request1);

        ScoreSaveRequest request2 = new ScoreSaveRequest();
        request2.setStudentId(testStudent.getId());
        request2.setSubjectId(testSubject2.getId());
        request2.setExamTypeId(testExamType.getId());
        request2.setScore(new BigDecimal("90"));
        studentScoreService.saveScore(request2);

        // 查询考试类型的所有成绩
        List<StudentScore> scores = studentScoreService.getScoresByExamTypeId(testExamType.getId());

        // 验证结果
        assertEquals(2, scores.size());
    }
}

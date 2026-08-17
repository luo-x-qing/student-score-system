package com.ecommerce.studentscorebackend.service;

import com.ecommerce.studentscorebackend.dto.*;
import com.ecommerce.studentscorebackend.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 总评计算服务测试
 *
 * 测试范围：
 * 1. 学科总评计算 - 正常情况
 * 2. 学科总评计算 - 缺失成绩
 * 3. 学科总评计算 - BigDecimal 精度测试
 * 4. 学科总评计算 - 四舍五入测试
 * 5. 综合得分计算 - 正常情况
 * 6. 综合得分计算 - 缺失学科总评
 * 7. 综合得分计算 - BigDecimal 精度测试
 *
 * 使用 SpringBootTest 进行集成测试
 * 每个测试方法都在事务中运行，测试后自动回滚
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ScoreCalculationServiceTest {

    @Autowired
    private ScoreCalculationService scoreCalculationService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private ExamTypeService examTypeService;

    @Autowired
    private StudentScoreService studentScoreService;

    private Student testStudent;
    private Subject testSubject;
    private ExamType examType1; // 期中考试 30%
    private ExamType examType2; // 期末考试 50%
    private ExamType examType3; // 平时成绩 20%

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

        // 创建考试类型
        ExamTypeCreateRequest examType1Request = new ExamTypeCreateRequest();
        examType1Request.setTypeName("期中考试");
        examType1Request.setRate(new BigDecimal("30"));
        examType1 = examTypeService.createExamType(examType1Request);

        ExamTypeCreateRequest examType2Request = new ExamTypeCreateRequest();
        examType2Request.setTypeName("期末考试");
        examType2Request.setRate(new BigDecimal("50"));
        examType2 = examTypeService.createExamType(examType2Request);

        ExamTypeCreateRequest examType3Request = new ExamTypeCreateRequest();
        examType3Request.setTypeName("平时成绩");
        examType3Request.setRate(new BigDecimal("20"));
        examType3 = examTypeService.createExamType(examType3Request);
    }

    /**
     * 测试：学科总评计算 - 正常情况
     *
     * 场景：
     * - 期中考试（30%）：85分
     * - 期末考试（50%）：90分
     * - 平时成绩（20%）：95分
     * - 预期总评 = 85 × 0.3 + 90 × 0.5 + 95 × 0.2 = 89.5
     */
    @Test
    void calculateSubjectTotalScore_withAllScores_shouldReturnCorrectTotal() {
        // 保存成绩
        saveScore(testStudent.getId(), testSubject.getId(), examType1.getId(), new BigDecimal("85"));
        saveScore(testStudent.getId(), testSubject.getId(), examType2.getId(), new BigDecimal("90"));
        saveScore(testStudent.getId(), testSubject.getId(), examType3.getId(), new BigDecimal("95"));

        // 计算总评
        SubjectTotalScoreResponse response = scoreCalculationService.calculateSubjectTotalScore(
                testStudent.getId(), testSubject.getId());

        // 验证结果
        assertNotNull(response);
        assertTrue(response.getComplete());
        assertEquals(0, new BigDecimal("89.5").compareTo(response.getTotalScore()));
    }

    /**
     * 测试：学科总评计算 - 缺失成绩
     *
     * 验证：缺少任何一个考试类型的成绩时，总评为 null，complete 为 false
     */
    @Test
    void calculateSubjectTotalScore_withMissingScore_shouldReturnNull() {
        // 只保存两个考试类型的成绩，缺少平时成绩
        saveScore(testStudent.getId(), testSubject.getId(), examType1.getId(), new BigDecimal("85"));
        saveScore(testStudent.getId(), testSubject.getId(), examType2.getId(), new BigDecimal("90"));

        // 计算总评
        SubjectTotalScoreResponse response = scoreCalculationService.calculateSubjectTotalScore(
                testStudent.getId(), testSubject.getId());

        // 验证结果
        assertNotNull(response);
        assertFalse(response.getComplete());
        assertNull(response.getTotalScore());
    }

    /**
     * 测试：学科总评计算 - BigDecimal 精度测试
     *
     * 场景：使用小数成绩测试精度
     * - 期中考试（30%）：85.5分
     * - 期末考试（50%）：90.3分
     * - 平时成绩（20%）：95.8分
     * - 预期总评 = 85.5 × 0.3 + 90.3 × 0.5 + 95.8 × 0.2 = 25.65 + 45.15 + 19.16 = 89.96
     */
    @Test
    void calculateSubjectTotalScore_withDecimalScores_shouldReturnCorrectTotal() {
        // 保存小数成绩
        saveScore(testStudent.getId(), testSubject.getId(), examType1.getId(), new BigDecimal("85.5"));
        saveScore(testStudent.getId(), testSubject.getId(), examType2.getId(), new BigDecimal("90.3"));
        saveScore(testStudent.getId(), testSubject.getId(), examType3.getId(), new BigDecimal("95.8"));

        // 计算总评
        SubjectTotalScoreResponse response = scoreCalculationService.calculateSubjectTotalScore(
                testStudent.getId(), testSubject.getId());

        // 验证结果
        assertNotNull(response);
        assertTrue(response.getComplete());
        assertEquals(0, new BigDecimal("89.96").compareTo(response.getTotalScore()));
    }

    /**
     * 测试：学科总评计算 - 四舍五入测试
     *
     * 场景：测试四舍五入到2位小数
     * - 期中考试（30%）：85.55分
     * - 期末考试（50%）：90.55分
     * - 平时成绩（20%）：95.55分
     * - 原始结果 = 85.55×0.3 + 90.55×0.5 + 95.55×0.2 = 25.665 + 45.275 + 19.11 = 90.05
     */
    @Test
    void calculateSubjectTotalScore_shouldRoundToTwoDecimals() {
        // 保存成绩
        saveScore(testStudent.getId(), testSubject.getId(), examType1.getId(), new BigDecimal("85.55"));
        saveScore(testStudent.getId(), testSubject.getId(), examType2.getId(), new BigDecimal("90.55"));
        saveScore(testStudent.getId(), testSubject.getId(), examType3.getId(), new BigDecimal("95.55"));

        // 计算总评
        SubjectTotalScoreResponse response = scoreCalculationService.calculateSubjectTotalScore(
                testStudent.getId(), testSubject.getId());

        // 验证结果：90.05
        assertNotNull(response);
        assertEquals(0, new BigDecimal("90.05").compareTo(response.getTotalScore()));
    }

    /**
     * 测试：综合得分计算 - 正常情况
     *
     * 场景：
     * - 语文总评：89.5，权重：1.5
     * - 数学总评：92.0，权重：1.5
     * - 预期综合得分 = 89.5 × 1.5 + 92.0 × 1.5 = 272.25
     */
    @Test
    void calculateComprehensiveScore_withAllSubjects_shouldReturnCorrectScore() {
        // 创建数学学科
        SubjectCreateRequest mathRequest = new SubjectCreateRequest();
        mathRequest.setSubjectName("数学");
        mathRequest.setWeightRate(new BigDecimal("1.5"));
        Subject mathSubject = subjectService.createSubject(mathRequest);

        // 语文成绩
        saveScore(testStudent.getId(), testSubject.getId(), examType1.getId(), new BigDecimal("85"));
        saveScore(testStudent.getId(), testSubject.getId(), examType2.getId(), new BigDecimal("90"));
        saveScore(testStudent.getId(), testSubject.getId(), examType3.getId(), new BigDecimal("95"));

        // 数学成绩
        saveScore(testStudent.getId(), mathSubject.getId(), examType1.getId(), new BigDecimal("90"));
        saveScore(testStudent.getId(), mathSubject.getId(), examType2.getId(), new BigDecimal("92"));
        saveScore(testStudent.getId(), mathSubject.getId(), examType3.getId(), new BigDecimal("95"));

        // 计算综合得分
        ComprehensiveScoreResponse response = scoreCalculationService.calculateComprehensiveScore(testStudent.getId());

        // 验证结果
        assertNotNull(response);
        assertTrue(response.getComplete());
        assertEquals(2, response.getSubjectScores().size());
        // 语文总评：89.5，数学总评：92.0
        // 综合得分 = 89.5 × 1.5 + 92.0 × 1.5 = 272.25
        assertEquals(0, new BigDecimal("272.25").compareTo(response.getComprehensiveScore()));
    }

    /**
     * 测试：综合得分计算 - 缺失学科总评
     *
     * 验证：缺少任何一个学科的总评时，综合得分为 null，complete 为 false
     */
    @Test
    void calculateComprehensiveScore_withMissingSubject_shouldReturnNull() {
        // 创建数学学科但不录入成绩
        SubjectCreateRequest mathRequest = new SubjectCreateRequest();
        mathRequest.setSubjectName("数学");
        mathRequest.setWeightRate(new BigDecimal("1.5"));
        subjectService.createSubject(mathRequest);

        // 只保存语文成绩
        saveScore(testStudent.getId(), testSubject.getId(), examType1.getId(), new BigDecimal("85"));
        saveScore(testStudent.getId(), testSubject.getId(), examType2.getId(), new BigDecimal("90"));
        saveScore(testStudent.getId(), testSubject.getId(), examType3.getId(), new BigDecimal("95"));

        // 计算综合得分
        ComprehensiveScoreResponse response = scoreCalculationService.calculateComprehensiveScore(testStudent.getId());

        // 验证结果
        assertNotNull(response);
        assertFalse(response.getComplete());
        assertNull(response.getComprehensiveScore());
        assertEquals(2, response.getSubjectScores().size());
    }

    /**
     * 测试：综合得分计算 - BigDecimal 精度测试
     *
     * 场景：使用小数权重测试精度
     */
    @Test
    void calculateComprehensiveScore_withDecimalWeights_shouldReturnCorrectScore() {
        // 创建英语学科（权重1.0）
        SubjectCreateRequest englishRequest = new SubjectCreateRequest();
        englishRequest.setSubjectName("英语");
        englishRequest.setWeightRate(new BigDecimal("1.0"));
        Subject englishSubject = subjectService.createSubject(englishRequest);

        // 语文成绩（总评89.5）
        saveScore(testStudent.getId(), testSubject.getId(), examType1.getId(), new BigDecimal("85"));
        saveScore(testStudent.getId(), testSubject.getId(), examType2.getId(), new BigDecimal("90"));
        saveScore(testStudent.getId(), testSubject.getId(), examType3.getId(), new BigDecimal("95"));

        // 英语成绩（总评88.0）
        saveScore(testStudent.getId(), englishSubject.getId(), examType1.getId(), new BigDecimal("85"));
        saveScore(testStudent.getId(), englishSubject.getId(), examType2.getId(), new BigDecimal("88"));
        saveScore(testStudent.getId(), englishSubject.getId(), examType3.getId(), new BigDecimal("92"));

        // 计算综合得分
        ComprehensiveScoreResponse response = scoreCalculationService.calculateComprehensiveScore(testStudent.getId());

        // 验证结果
        // 英语总评：85×0.3 + 88×0.5 + 92×0.2 = 25.5 + 44 + 18.4 = 87.9
        // 语文总评：89.5，权重：1.5
        // 英语总评：87.9，权重：1.0
        // 综合得分 = 89.5 × 1.5 + 87.9 × 1.0 = 134.25 + 87.9 = 222.15
        assertNotNull(response);
        assertTrue(response.getComplete());
        assertEquals(0, new BigDecimal("222.15").compareTo(response.getComprehensiveScore()));
    }

    /**
     * 辅助方法：保存成绩
     */
    private void saveScore(Long studentId, Long subjectId, Long examTypeId, BigDecimal score) {
        ScoreSaveRequest request = new ScoreSaveRequest();
        request.setStudentId(studentId);
        request.setSubjectId(subjectId);
        request.setExamTypeId(examTypeId);
        request.setScore(score);
        studentScoreService.saveScore(request);
    }
}

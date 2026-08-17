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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 排名服务测试
 *
 * 测试范围：
 * 1. 稠密排名算法 - 固定样例（1、1、2）
 * 2. 按学科总评排名 - 降序
 * 3. 按学科总评排名 - 升序
 * 4. 按学科总评排名 - 未完成成绩的学生
 * 5. 按综合得分排名 - 降序
 * 6. 按综合得分排名 - 班级隔离
 *
 * 使用 SpringBootTest 进行集成测试
 * 每个测试方法都在事务中运行，测试后自动回滚
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RankingServiceTest {

    @Autowired
    private RankingService rankingService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private ExamTypeService examTypeService;

    @Autowired
    private StudentScoreService studentScoreService;

    private Subject testSubject;
    private ExamType examType1;

    /**
     * 每个测试前初始化测试数据
     */
    @BeforeEach
    void setUp() {
        // 创建测试学科
        SubjectCreateRequest subjectRequest = new SubjectCreateRequest();
        subjectRequest.setSubjectName("语文");
        subjectRequest.setWeightRate(new BigDecimal("1.5"));
        testSubject = subjectService.createSubject(subjectRequest);

        // 创建考试类型（100%）
        ExamTypeCreateRequest examTypeRequest = new ExamTypeCreateRequest();
        examTypeRequest.setTypeName("期末考试");
        examTypeRequest.setRate(new BigDecimal("100"));
        examType1 = examTypeService.createExamType(examTypeRequest);
    }

    /**
     * 测试：稠密排名算法 - 固定样例（1、1、2）
     *
     * 场景：
     * - 学生A：100分，排名1
     * - 学生B：100分，排名1（相同分数）
     * - 学生C：90分，排名2（不跳号）
     *
     * 验证稠密排名（Dense Rank）：相同分数排名相同，下一个排名不跳号
     */
    @Test
    void rankBySubjectTotalScore_withDenseRank_shouldReturn1_1_2() {
        // 创建3个学生
        Student studentA = createStudent("2024001", "张三", "一年级1班");
        Student studentB = createStudent("2024002", "李四", "一年级1班");
        Student studentC = createStudent("2024003", "王五", "一年级1班");

        // 保存成绩
        saveScore(studentA.getId(), testSubject.getId(), examType1.getId(), new BigDecimal("100"));
        saveScore(studentB.getId(), testSubject.getId(), examType1.getId(), new BigDecimal("100"));
        saveScore(studentC.getId(), testSubject.getId(), examType1.getId(), new BigDecimal("90"));

        // 查询排名
        List<RankingResponse> rankings = rankingService.rankBySubjectTotalScore(
                testSubject.getId(), null, false);

        // 验证结果：1、1、2
        assertEquals(3, rankings.size());
        assertEquals(1, rankings.get(0).getRank()); // 100分，排名1
        assertEquals(1, rankings.get(1).getRank()); // 100分，排名1
        assertEquals(2, rankings.get(2).getRank()); // 90分，排名2（不跳号）
    }

    /**
     * 测试：按学科总评排名 - 降序（默认）
     *
     * 验证：分数高的排在前面
     */
    @Test
    void rankBySubjectTotalScore_descending_shouldOrderByScoreDesc() {
        // 创建3个学生
        Student studentA = createStudent("2024001", "张三", "一年级1班");
        Student studentB = createStudent("2024002", "李四", "一年级1班");
        Student studentC = createStudent("2024003", "王五", "一年级1班");

        // 保存成绩（故意打乱顺序）
        saveScore(studentA.getId(), testSubject.getId(), examType1.getId(), new BigDecimal("85"));
        saveScore(studentB.getId(), testSubject.getId(), examType1.getId(), new BigDecimal("95"));
        saveScore(studentC.getId(), testSubject.getId(), examType1.getId(), new BigDecimal("90"));

        // 查询排名（降序）
        List<RankingResponse> rankings = rankingService.rankBySubjectTotalScore(
                testSubject.getId(), null, false);

        // 验证结果：95、90、85
        assertEquals(3, rankings.size());
        assertEquals(0, new BigDecimal("95").compareTo(rankings.get(0).getScore()));
        assertEquals(0, new BigDecimal("90").compareTo(rankings.get(1).getScore()));
        assertEquals(0, new BigDecimal("85").compareTo(rankings.get(2).getScore()));
        assertEquals(1, rankings.get(0).getRank());
        assertEquals(2, rankings.get(1).getRank());
        assertEquals(3, rankings.get(2).getRank());
    }

    /**
     * 测试：按学科总评排名 - 升序
     *
     * 验证：分数低的排在前面
     */
    @Test
    void rankBySubjectTotalScore_ascending_shouldOrderByScoreAsc() {
        // 创建3个学生
        Student studentA = createStudent("2024001", "张三", "一年级1班");
        Student studentB = createStudent("2024002", "李四", "一年级1班");
        Student studentC = createStudent("2024003", "王五", "一年级1班");

        // 保存成绩
        saveScore(studentA.getId(), testSubject.getId(), examType1.getId(), new BigDecimal("85"));
        saveScore(studentB.getId(), testSubject.getId(), examType1.getId(), new BigDecimal("95"));
        saveScore(studentC.getId(), testSubject.getId(), examType1.getId(), new BigDecimal("90"));

        // 查询排名（升序）
        List<RankingResponse> rankings = rankingService.rankBySubjectTotalScore(
                testSubject.getId(), null, true);

        // 验证结果：85、90、95
        assertEquals(3, rankings.size());
        assertEquals(0, new BigDecimal("85").compareTo(rankings.get(0).getScore()));
        assertEquals(0, new BigDecimal("90").compareTo(rankings.get(1).getScore()));
        assertEquals(0, new BigDecimal("95").compareTo(rankings.get(2).getScore()));
        assertEquals(1, rankings.get(0).getRank());
        assertEquals(2, rankings.get(1).getRank());
        assertEquals(3, rankings.get(2).getRank());
    }

    /**
     * 测试：按学科总评排名 - 未完成成绩的学生
     *
     * 验证：未完成成绩的学生 rank 为 null，排在最后
     */
    @Test
    void rankBySubjectTotalScore_withIncomplete_shouldHaveNullRank() {
        // 创建3个学生
        Student studentA = createStudent("2024001", "张三", "一年级1班");
        Student studentB = createStudent("2024002", "李四", "一年级1班");
        Student studentC = createStudent("2024003", "王五", "一年级1班");

        // 只保存两个学生的成绩
        saveScore(studentA.getId(), testSubject.getId(), examType1.getId(), new BigDecimal("95"));
        saveScore(studentB.getId(), testSubject.getId(), examType1.getId(), new BigDecimal("90"));
        // studentC 没有成绩

        // 查询排名
        List<RankingResponse> rankings = rankingService.rankBySubjectTotalScore(
                testSubject.getId(), null, false);

        // 验证结果
        assertEquals(3, rankings.size());
        // 前两个有排名
        assertEquals(1, rankings.get(0).getRank());
        assertEquals(2, rankings.get(1).getRank());
        // 最后一个未完成，rank 为 null
        assertNull(rankings.get(2).getRank());
        assertNull(rankings.get(2).getScore());
        assertFalse(rankings.get(2).getComplete());
    }

    /**
     * 测试：按综合得分排名 - 降序
     */
    @Test
    void rankByComprehensiveScore_descending_shouldOrderByScoreDesc() {
        // 创建3个学生
        Student studentA = createStudent("2024001", "张三", "一年级1班");
        Student studentB = createStudent("2024002", "李四", "一年级1班");
        Student studentC = createStudent("2024003", "王五", "一年级1班");

        // 保存成绩
        saveScore(studentA.getId(), testSubject.getId(), examType1.getId(), new BigDecimal("85"));
        saveScore(studentB.getId(), testSubject.getId(), examType1.getId(), new BigDecimal("95"));
        saveScore(studentC.getId(), testSubject.getId(), examType1.getId(), new BigDecimal("90"));

        // 查询排名（降序）
        List<RankingResponse> rankings = rankingService.rankByComprehensiveScore(null, false);

        // 验证结果
        assertEquals(3, rankings.size());
        // 综合得分 = 学科总评 × 权重1.5
        assertEquals(0, new BigDecimal("142.50").compareTo(rankings.get(0).getScore())); // 95 × 1.5
        assertEquals(0, new BigDecimal("135.00").compareTo(rankings.get(1).getScore())); // 90 × 1.5
        assertEquals(0, new BigDecimal("127.50").compareTo(rankings.get(2).getScore())); // 85 × 1.5
    }

    /**
     * 测试：按学科总评排名 - 班级隔离
     *
     * 验证：只返回指定班级的学生排名
     */
    @Test
    void rankBySubjectTotalScore_withClassName_shouldFilterByClass() {
        // 创建不同班级的学生
        Student student1 = createStudent("2024001", "张三", "一年级1班");
        Student student2 = createStudent("2024002", "李四", "一年级1班");
        Student student3 = createStudent("2024003", "王五", "一年级2班");

        // 保存成绩
        saveScore(student1.getId(), testSubject.getId(), examType1.getId(), new BigDecimal("85"));
        saveScore(student2.getId(), testSubject.getId(), examType1.getId(), new BigDecimal("90"));
        saveScore(student3.getId(), testSubject.getId(), examType1.getId(), new BigDecimal("95"));

        // 查询一年级1班的排名
        List<RankingResponse> rankings = rankingService.rankBySubjectTotalScore(
                testSubject.getId(), "一年级1班", false);

        // 验证结果：只有2个学生
        assertEquals(2, rankings.size());
        assertEquals("一年级1班", rankings.get(0).getClassName());
        assertEquals("一年级1班", rankings.get(1).getClassName());
    }

    /**
     * 辅助方法：创建学生
     */
    private Student createStudent(String studentNo, String name, String className) {
        StudentCreateRequest request = new StudentCreateRequest();
        request.setStudentNo(studentNo);
        request.setName(name);
        request.setClassName(className);
        return studentService.createStudent(request);
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

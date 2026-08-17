package com.ecommerce.studentscorebackend.service.impl;

import com.ecommerce.studentscorebackend.dto.ComprehensiveScoreResponse;
import com.ecommerce.studentscorebackend.dto.SubjectTotalScoreResponse;
import com.ecommerce.studentscorebackend.entity.ExamType;
import com.ecommerce.studentscorebackend.entity.StudentScore;
import com.ecommerce.studentscorebackend.entity.Subject;
import com.ecommerce.studentscorebackend.service.ExamTypeService;
import com.ecommerce.studentscorebackend.service.ScoreCalculationService;
import com.ecommerce.studentscorebackend.service.StudentScoreService;
import com.ecommerce.studentscorebackend.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 总评计算服务实现类
 *
 * 使用 BigDecimal 进行精确计算，避免浮点数精度问题
 * 所有计算结果四舍五入保留2位小数
 */
@Service
public class ScoreCalculationServiceImpl implements ScoreCalculationService {

    @Autowired
    private StudentScoreService studentScoreService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private ExamTypeService examTypeService;

    /**
     * 计算学生在某个学科的总评成绩
     *
     * 实现步骤：
     * 1. 获取该学科的所有考试类型
     * 2. 获取该学生在该学科下所有考试类型的成绩
     * 3. 检查是否所有考试类型都有成绩
     * 4. 如果完整，则计算总评：Σ(考试成绩 × 考试类型比率%)
     * 5. 四舍五入保留2位小数
     *
     * @param studentId 学生ID
     * @param subjectId 学科ID
     * @return 学科总评响应
     */
    @Override
    public SubjectTotalScoreResponse calculateSubjectTotalScore(Long studentId, Long subjectId) {
        SubjectTotalScoreResponse response = new SubjectTotalScoreResponse();
        response.setStudentId(studentId);
        response.setSubjectId(subjectId);

        // 获取学科信息
        Subject subject = subjectService.getSubjectById(subjectId);
        if (subject == null) {
            throw new IllegalArgumentException("学科不存在，ID: " + subjectId);
        }
        response.setSubjectName(subject.getSubjectName());

        // 获取所有考试类型
        List<ExamType> examTypes = examTypeService.getAllExamTypes();
        if (examTypes.isEmpty()) {
            response.setComplete(false);
            response.setTotalScore(null);
            return response;
        }

        // 获取该学生在该学科的所有成绩
        List<StudentScore> scores = studentScoreService.getScoresByStudentId(studentId).stream()
                .filter(score -> score.getSubjectId().equals(subjectId))
                .collect(Collectors.toList());

        // 构建成绩映射：考试类型ID -> 成绩
        Map<Long, BigDecimal> scoreMap = scores.stream()
                .filter(score -> score.getScore() != null)
                .collect(Collectors.toMap(
                        StudentScore::getExamTypeId,
                        StudentScore::getScore
                ));

        // 检查是否所有考试类型都有成绩
        boolean complete = examTypes.stream()
                .allMatch(examType -> scoreMap.containsKey(examType.getId()));

        response.setComplete(complete);

        if (!complete) {
            // 缺少成绩，总评为 null
            response.setTotalScore(null);
            return response;
        }

        // 计算总评：Σ(考试成绩 × 考试类型比率%)
        BigDecimal totalScore = BigDecimal.ZERO;
        for (ExamType examType : examTypes) {
            BigDecimal score = scoreMap.get(examType.getId());
            BigDecimal rate = examType.getRate().divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP);
            totalScore = totalScore.add(score.multiply(rate));
        }

        // 四舍五入保留2位小数
        response.setTotalScore(totalScore.setScale(2, RoundingMode.HALF_UP));

        return response;
    }

    /**
     * 计算学生的综合得分
     *
     * 实现步骤：
     * 1. 获取所有学科
     * 2. 计算该学生在每个学科的总评
     * 3. 检查是否所有学科都有总评
     * 4. 如果完整，则计算综合得分：Σ(学科总评 × 学科权重)
     * 5. 四舍五入保留2位小数
     *
     * @param studentId 学生ID
     * @return 综合得分响应
     */
    @Override
    public ComprehensiveScoreResponse calculateComprehensiveScore(Long studentId) {
        ComprehensiveScoreResponse response = new ComprehensiveScoreResponse();
        response.setStudentId(studentId);

        // 获取所有学科
        List<Subject> subjects = subjectService.getAllSubjects();
        if (subjects.isEmpty()) {
            response.setComplete(false);
            response.setComprehensiveScore(null);
            response.setSubjectScores(new ArrayList<>());
            return response;
        }

        // 计算每个学科的总评
        List<SubjectTotalScoreResponse> subjectScores = new ArrayList<>();
        for (Subject subject : subjects) {
            SubjectTotalScoreResponse subjectScore = calculateSubjectTotalScore(studentId, subject.getId());
            subjectScores.add(subjectScore);
        }
        response.setSubjectScores(subjectScores);

        // 检查是否所有学科都有总评
        boolean complete = subjectScores.stream()
                .allMatch(SubjectTotalScoreResponse::getComplete);

        response.setComplete(complete);

        if (!complete) {
            // 缺少学科总评，综合得分为 null
            response.setComprehensiveScore(null);
            return response;
        }

        // 计算综合得分：Σ(学科总评 × 学科权重)
        BigDecimal comprehensiveScore = BigDecimal.ZERO;
        for (int i = 0; i < subjects.size(); i++) {
            Subject subject = subjects.get(i);
            SubjectTotalScoreResponse subjectScore = subjectScores.get(i);
            comprehensiveScore = comprehensiveScore.add(
                    subjectScore.getTotalScore().multiply(subject.getWeightRate())
            );
        }

        // 四舍五入保留2位小数
        response.setComprehensiveScore(comprehensiveScore.setScale(2, RoundingMode.HALF_UP));

        return response;
    }
}

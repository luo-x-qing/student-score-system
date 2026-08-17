package com.ecommerce.studentscorebackend.service.impl;

import com.ecommerce.studentscorebackend.dto.ComprehensiveScoreResponse;
import com.ecommerce.studentscorebackend.dto.RankingResponse;
import com.ecommerce.studentscorebackend.dto.SubjectTotalScoreResponse;
import com.ecommerce.studentscorebackend.entity.Student;
import com.ecommerce.studentscorebackend.service.RankingService;
import com.ecommerce.studentscorebackend.service.ScoreCalculationService;
import com.ecommerce.studentscorebackend.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 排名服务实现类
 *
 * 实现稠密排名算法（Dense Rank）
 */
@Service
public class RankingServiceImpl implements RankingService {

    @Autowired
    private StudentService studentService;

    @Autowired
    private ScoreCalculationService scoreCalculationService;

    /**
     * 按学科总评排名
     *
     * 实现步骤：
     * 1. 获取学生列表（根据班级筛选）
     * 2. 计算每个学生的学科总评
     * 3. 分离完成和未完成的学生
     * 4. 对完成的学生按成绩排序
     * 5. 使用稠密排名算法分配排名
     * 6. 未完成的学生排在最后，rank 为 null
     *
     * @param subjectId 学科ID
     * @param className 班级名称（可选）
     * @param ascending 是否升序排序（默认降序）
     * @return 排名列表
     */
    @Override
    public List<RankingResponse> rankBySubjectTotalScore(Long subjectId, String className, Boolean ascending) {
        // 1. 获取学生列表
        List<Student> students = getAllStudents(className);

        // 2. 计算每个学生的学科总评并构建响应
        List<RankingResponse> rankings = new ArrayList<>();
        for (Student student : students) {
            SubjectTotalScoreResponse totalScore = scoreCalculationService.calculateSubjectTotalScore(
                    student.getId(), subjectId);

            RankingResponse ranking = new RankingResponse();
            ranking.setStudentId(student.getId());
            ranking.setStudentNo(student.getStudentNo());
            ranking.setStudentName(student.getName());
            ranking.setClassName(student.getClassName());
            ranking.setScore(totalScore.getTotalScore());
            ranking.setComplete(totalScore.getComplete());
            rankings.add(ranking);
        }

        // 3. 分离完成和未完成的学生
        List<RankingResponse> completed = rankings.stream()
                .filter(r -> r.getScore() != null)
                .collect(Collectors.toList());

        List<RankingResponse> incomplete = rankings.stream()
                .filter(r -> r.getScore() == null)
                .collect(Collectors.toList());

        // 4. 对完成的学生按成绩排序
        boolean isAscending = ascending != null && ascending;
        completed.sort(isAscending
                ? Comparator.comparing(RankingResponse::getScore)
                : Comparator.comparing(RankingResponse::getScore).reversed());

        // 5. 使用稠密排名算法分配排名
        assignDenseRank(completed);

        // 6. 未完成的学生 rank 为 null
        for (RankingResponse r : incomplete) {
            r.setRank(null);
        }

        // 7. 合并结果：完成的在前，未完成的在后
        List<RankingResponse> result = new ArrayList<>(completed);
        result.addAll(incomplete);

        return result;
    }

    /**
     * 按综合得分排名
     *
     * 实现步骤：
     * 1. 获取学生列表（根据班级筛选）
     * 2. 计算每个学生的综合得分
     * 3. 分离完成和未完成的学生
     * 4. 对完成的学生按成绩排序
     * 5. 使用稠密排名算法分配排名
     * 6. 未完成的学生排在最后，rank 为 null
     *
     * @param className 班级名称（可选）
     * @param ascending 是否升序排序（默认降序）
     * @return 排名列表
     */
    @Override
    public List<RankingResponse> rankByComprehensiveScore(String className, Boolean ascending) {
        // 1. 获取学生列表
        List<Student> students = getAllStudents(className);

        // 2. 计算每个学生的综合得分并构建响应
        List<RankingResponse> rankings = new ArrayList<>();
        for (Student student : students) {
            ComprehensiveScoreResponse comprehensiveScore = scoreCalculationService.calculateComprehensiveScore(
                    student.getId());

            RankingResponse ranking = new RankingResponse();
            ranking.setStudentId(student.getId());
            ranking.setStudentNo(student.getStudentNo());
            ranking.setStudentName(student.getName());
            ranking.setClassName(student.getClassName());
            ranking.setScore(comprehensiveScore.getComprehensiveScore());
            ranking.setComplete(comprehensiveScore.getComplete());
            rankings.add(ranking);
        }

        // 3. 分离完成和未完成的学生
        List<RankingResponse> completed = rankings.stream()
                .filter(r -> r.getScore() != null)
                .collect(Collectors.toList());

        List<RankingResponse> incomplete = rankings.stream()
                .filter(r -> r.getScore() == null)
                .collect(Collectors.toList());

        // 4. 对完成的学生按成绩排序
        boolean isAscending = ascending != null && ascending;
        completed.sort(isAscending
                ? Comparator.comparing(RankingResponse::getScore)
                : Comparator.comparing(RankingResponse::getScore).reversed());

        // 5. 使用稠密排名算法分配排名
        assignDenseRank(completed);

        // 6. 未完成的学生 rank 为 null
        for (RankingResponse r : incomplete) {
            r.setRank(null);
        }

        // 7. 合并结果：完成的在前，未完成的在后
        List<RankingResponse> result = new ArrayList<>(completed);
        result.addAll(incomplete);

        return result;
    }

    /**
     * 获取所有学生（支持按班级筛选）
     *
     * @param className 班级名称（可选）
     * @return 学生列表
     */
    private List<Student> getAllStudents(String className) {
        if (StringUtils.hasText(className)) {
            // 按班级筛选
            return studentService.queryStudents(new com.ecommerce.studentscorebackend.dto.StudentQueryRequest() {{
                setClassName(className);
                setPage(1);
                setPageSize(1000);
            }}).getRecords();
        } else {
            // 查询所有学生
            return studentService.queryStudents(new com.ecommerce.studentscorebackend.dto.StudentQueryRequest() {{
                setPage(1);
                setPageSize(1000);
            }}).getRecords();
        }
    }

    /**
     * 使用稠密排名算法分配排名
     *
     * 稠密排名（Dense Rank）规则：
     * - 相同分数的学生排名相同
     * - 下一个排名不跳号
     * - 例如：100分2人排名1，90分1人排名2（不是3）
     *
     * @param rankings 已排序的排名列表
     */
    private void assignDenseRank(List<RankingResponse> rankings) {
        if (rankings.isEmpty()) {
            return;
        }

        int currentRank = 1;
        BigDecimal previousScore = null;

        for (RankingResponse ranking : rankings) {
            if (previousScore != null && ranking.getScore().compareTo(previousScore) != 0) {
                // 分数变化，排名递增
                currentRank++;
            }
            ranking.setRank(currentRank);
            previousScore = ranking.getScore();
        }
    }
}

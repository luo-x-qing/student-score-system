package com.ecommerce.studentscorebackend.service;

import com.ecommerce.studentscorebackend.dto.ComprehensiveScoreResponse;
import com.ecommerce.studentscorebackend.dto.SubjectTotalScoreResponse;

/**
 * 总评计算服务接口
 *
 * 职责：
 * 1. 计算学科总评：学科总评 = Σ(考试成绩 × 考试类型比率%)
 * 2. 计算综合得分：综合得分 = Σ(学科总评 × 学科权重)
 * 3. 处理缺失成绩：如果缺少任何一个成绩，则相应的总评/综合得分为 null
 * 4. BigDecimal 精确计算，四舍五入保留2位小数
 */
public interface ScoreCalculationService {

    /**
     * 计算学生在某个学科的总评成绩
     *
     * 计算公式：学科总评 = Σ(考试成绩 × 考试类型比率%)
     *
     * 业务规则：
     * 1. 获取该学科的所有考试类型
     * 2. 获取该学生在该学科下所有考试类型的成绩
     * 3. 如果缺少任何一个考试类型的成绩，则总评为 null
     * 4. 使用 BigDecimal 精确计算，四舍五入保留2位小数
     *
     * 例如：
     * - 期中考试（30%）：85分
     * - 期末考试（50%）：90分
     * - 平时成绩（20%）：95分
     * - 学科总评 = 85 × 0.3 + 90 × 0.5 + 95 × 0.2 = 89.5
     *
     * @param studentId 学生ID
     * @param subjectId 学科ID
     * @return 学科总评响应，包含总评成绩和是否完整标识
     */
    SubjectTotalScoreResponse calculateSubjectTotalScore(Long studentId, Long subjectId);

    /**
     * 计算学生的综合得分
     *
     * 计算公式：综合得分 = Σ(学科总评 × 学科权重)
     *
     * 业务规则：
     * 1. 获取所有学科
     * 2. 计算该学生在每个学科的总评
     * 3. 如果缺少任何一个学科的总评，则综合得分为 null
     * 4. 使用 BigDecimal 精确计算，四舍五入保留2位小数
     *
     * 例如：
     * - 语文总评：89.5，权重：1.5
     * - 数学总评：92.0，权重：1.5
     * - 英语总评：88.0，权重：1.0
     * - 综合得分 = 89.5 × 1.5 + 92.0 × 1.5 + 88.0 × 1.0 = 360.25
     *
     * @param studentId 学生ID
     * @return 综合得分响应，包含综合得分、是否完整标识和各学科总评明细
     */
    ComprehensiveScoreResponse calculateComprehensiveScore(Long studentId);
}

package com.ecommerce.studentscorebackend.controller;

import com.ecommerce.studentscorebackend.dto.SubjectCreateRequest;
import com.ecommerce.studentscorebackend.dto.SubjectUpdateRequest;
import com.ecommerce.studentscorebackend.entity.Subject;
import com.ecommerce.studentscorebackend.service.SubjectService;
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
 * 学科控制器集成测试
 *
 * 测试范围：
 * 1. 创建学科 - 正常情况
 * 2. 创建学科 - 学科名称重复
 * 3. 创建学科 - 权重为负数（校验失败）
 * 4. 查询学科 - 存在
 * 5. 查询学科 - 不存在
 * 6. 更新学科 - 正常情况
 * 7. 更新学科 - 学科名称重复
 * 8. 更新学科 - 学科不存在
 * 9. 删除学科 - 正常情况
 * 10. 删除学科 - 学科不存在
 * 11. 查询所有学科 - 按名称排序
 *
 * 使用 SpringBootTest 进行集成测试
 * 每个测试方法都在事务中运行，测试后自动回滚
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SubjectControllerTest {

    @Autowired
    private SubjectService subjectService;

    private SubjectCreateRequest validRequest;

    /**
     * 每个测试前初始化测试数据
     */
    @BeforeEach
    void setUp() {
        // 准备有效的创建请求
        validRequest = new SubjectCreateRequest();
        validRequest.setSubjectName("语文");
        validRequest.setWeightRate(new BigDecimal("1.5"));
    }

    /**
     * 测试：创建学科 - 正常情况
     *
     * 验证：
     * 1. 创建成功返回学科实体
     * 2. ID 自动生成
     * 3. 学科名称、权重正确
     */
    @Test
    void createSubject_withValidData_shouldReturnCreatedSubject() {
        // 创建学科
        Subject subject = subjectService.createSubject(validRequest);

        // 验证结果
        assertNotNull(subject);
        assertNotNull(subject.getId(), "ID应该自动生成");
        assertEquals("语文", subject.getSubjectName());
        // 使用 compareTo 比较 BigDecimal，忽略精度差异
        assertEquals(0, new BigDecimal("1.5").compareTo(subject.getWeightRate()));
    }

    /**
     * 测试：创建学科 - 学科名称重复
     *
     * 验证：
     * 1. 第一次创建成功
     * 2. 第二次创建相同名称时抛出 IllegalArgumentException
     * 3. 异常消息包含"学科名称已存在"
     */
    @Test
    void createSubject_withDuplicateName_shouldThrowException() {
        // 第一次创建成功
        subjectService.createSubject(validRequest);

        // 第二次创建相同名称，预期抛出异常
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> subjectService.createSubject(validRequest)
        );

        // 验证异常消息
        assertTrue(exception.getMessage().contains("学科名称已存在"));
    }

    /**
     * 测试：创建学科 - 权重为零
     *
     * 验证：
     * 权重为0时应该抛出异常（@Positive 校验）
     */
    @Test
    void createSubject_withZeroWeight_shouldFail() {
        validRequest.setWeightRate(BigDecimal.ZERO);

        // 由于 @Positive 校验在 Controller 层，这里直接测试业务逻辑
        // 实际应用中会被 Spring Validation 拦截
        assertThrows(
                Exception.class,
                () -> subjectService.createSubject(validRequest)
        );
    }

    /**
     * 测试：查询学科 - 学科存在
     *
     * 验证：
     * 1. 能够正确查询到已创建的学科
     * 2. 查询结果与创建时一致
     */
    @Test
    void getSubject_whenExists_shouldReturnSubject() {
        // 先创建学科
        Subject created = subjectService.createSubject(validRequest);

        // 根据ID查询
        Subject found = subjectService.getSubjectById(created.getId());

        // 验证查询结果
        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
        assertEquals(created.getSubjectName(), found.getSubjectName());
        // 使用 compareTo 比较 BigDecimal，忽略精度差异
        assertEquals(0, created.getWeightRate().compareTo(found.getWeightRate()));
    }

    /**
     * 测试：查询学科 - 学科不存在
     *
     * 验证：
     * 1. 查询不存在的ID时返回 null
     */
    @Test
    void getSubject_whenNotExists_shouldReturnNull() {
        // 查询不存在的ID
        Subject found = subjectService.getSubjectById(999999L);

        // 验证返回 null
        assertNull(found);
    }

    /**
     * 测试：更新学科 - 正常情况
     *
     * 验证：
     * 1. 更新成功
     * 2. 更新后的字段正确
     */
    @Test
    void updateSubject_withValidData_shouldReturnUpdatedSubject() {
        // 先创建学科
        Subject created = subjectService.createSubject(validRequest);

        // 准备更新请求
        SubjectUpdateRequest updateRequest = new SubjectUpdateRequest();
        updateRequest.setSubjectName("数学");
        updateRequest.setWeightRate(new BigDecimal("2.0"));

        // 执行更新
        Subject updated = subjectService.updateSubject(created.getId(), updateRequest);

        // 验证更新结果
        assertNotNull(updated);
        assertEquals(created.getId(), updated.getId());
        assertEquals("数学", updated.getSubjectName());
        // 使用 compareTo 比较 BigDecimal，忽略精度差异
        assertEquals(0, new BigDecimal("2.0").compareTo(updated.getWeightRate()));
    }

    /**
     * 测试：更新学科 - 学科名称重复
     *
     * 验证：
     * 1. 创建两个学科
     * 2. 更新第二个学科的名称为第一个学科的名称时抛出异常
     */
    @Test
    void updateSubject_withDuplicateName_shouldThrowException() {
        // 创建第一个学科
        subjectService.createSubject(validRequest);

        // 创建第二个学科
        SubjectCreateRequest request2 = new SubjectCreateRequest();
        request2.setSubjectName("数学");
        request2.setWeightRate(new BigDecimal("1.0"));
        Subject subject2 = subjectService.createSubject(request2);

        // 尝试将第二个学科的名称改为第一个学科的名称
        SubjectUpdateRequest updateRequest = new SubjectUpdateRequest();
        updateRequest.setSubjectName("语文");
        updateRequest.setWeightRate(new BigDecimal("1.0"));

        // 预期抛出异常
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> subjectService.updateSubject(subject2.getId(), updateRequest)
        );

        // 验证异常消息
        assertTrue(exception.getMessage().contains("学科名称已存在"));
    }

    /**
     * 测试：更新学科 - 学科不存在
     *
     * 验证：
     * 1. 更新不存在的学科时抛出 IllegalArgumentException
     * 2. 异常消息包含"学科不存在"
     */
    @Test
    void updateSubject_whenNotExists_shouldThrowException() {
        // 准备更新请求
        SubjectUpdateRequest updateRequest = new SubjectUpdateRequest();
        updateRequest.setSubjectName("数学");
        updateRequest.setWeightRate(new BigDecimal("1.0"));

        // 更新不存在的学科，预期抛出异常
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> subjectService.updateSubject(999999L, updateRequest)
        );

        // 验证异常消息
        assertTrue(exception.getMessage().contains("学科不存在"));
    }

    /**
     * 测试：删除学科 - 正常情况（无成绩引用）
     *
     * 验证：
     * 1. 删除成功
     * 2. 删除后查询不到该学科
     */
    @Test
    void deleteSubject_withoutScores_shouldSucceed() {
        // 先创建学科
        Subject created = subjectService.createSubject(validRequest);

        // 执行删除
        subjectService.deleteSubject(created.getId());

        // 验证删除成功：查询不到该学科
        Subject found = subjectService.getSubjectById(created.getId());
        assertNull(found, "删除后应该查询不到该学科");
    }

    /**
     * 测试：删除学科 - 学科不存在
     *
     * 验证：
     * 1. 删除不存在的学科时抛出 IllegalArgumentException
     * 2. 异常消息包含"学科不存在"
     */
    @Test
    void deleteSubject_whenNotExists_shouldThrowException() {
        // 删除不存在的学科，预期抛出异常
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> subjectService.deleteSubject(999999L)
        );

        // 验证异常消息
        assertTrue(exception.getMessage().contains("学科不存在"));
    }

    /**
     * 测试：查询所有学科 - 按名称排序
     *
     * 验证：
     * 1. 返回所有学科
     * 2. 按学科名称升序排序
     */
    @Test
    void getAllSubjects_shouldReturnSortedList() {
        // 创建多个学科（故意打乱顺序）
        SubjectCreateRequest request1 = new SubjectCreateRequest();
        request1.setSubjectName("英语");
        request1.setWeightRate(new BigDecimal("1.5"));
        subjectService.createSubject(request1);

        SubjectCreateRequest request2 = new SubjectCreateRequest();
        request2.setSubjectName("数学");
        request2.setWeightRate(new BigDecimal("1.5"));
        subjectService.createSubject(request2);

        SubjectCreateRequest request3 = new SubjectCreateRequest();
        request3.setSubjectName("语文");
        request3.setWeightRate(new BigDecimal("1.5"));
        subjectService.createSubject(request3);

        // 查询所有学科
        List<Subject> subjects = subjectService.getAllSubjects();

        // 验证结果
        assertNotNull(subjects);
        assertEquals(3, subjects.size());

        // 验证按名称升序排序：数学 < 英语 < 语文
        assertEquals("数学", subjects.get(0).getSubjectName());
        assertEquals("英语", subjects.get(1).getSubjectName());
        assertEquals("语文", subjects.get(2).getSubjectName());
    }
}

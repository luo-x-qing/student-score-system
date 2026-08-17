package com.ecommerce.studentscorebackend.controller;

import com.ecommerce.studentscorebackend.dto.ExamTypeCreateRequest;
import com.ecommerce.studentscorebackend.dto.ExamTypeUpdateRequest;
import com.ecommerce.studentscorebackend.entity.ExamType;
import com.ecommerce.studentscorebackend.service.ExamTypeService;
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
 * 考试类型控制器集成测试
 *
 * 测试范围：
 * 1. 创建考试类型 - 正常情况
 * 2. 创建考试类型 - 考试类型名称重复
 * 3. 创建考试类型 - 比率超出范围
 * 4. 查询考试类型 - 存在
 * 5. 查询考试类型 - 不存在
 * 6. 更新考试类型 - 正常情况
 * 7. 更新考试类型 - 考试类型名称重复
 * 8. 更新考试类型 - 考试类型不存在
 * 9. 删除考试类型 - 正常情况
 * 10. 删除考试类型 - 考试类型不存在
 * 11. 查询所有考试类型 - 按名称排序
 *
 * 使用 SpringBootTest 进行集成测试
 * 每个测试方法都在事务中运行，测试后自动回滚
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ExamTypeControllerTest {

    @Autowired
    private ExamTypeService examTypeService;

    private ExamTypeCreateRequest validRequest;

    /**
     * 每个测试前初始化测试数据
     */
    @BeforeEach
    void setUp() {
        // 准备有效的创建请求
        validRequest = new ExamTypeCreateRequest();
        validRequest.setTypeName("期中考试");
        validRequest.setRate(new BigDecimal("30"));
    }

    /**
     * 测试：创建考试类型 - 正常情况
     *
     * 验证：
     * 1. 创建成功返回考试类型实体
     * 2. ID 自动生成
     * 3. 考试类型名称、比率正确
     */
    @Test
    void createExamType_withValidData_shouldReturnCreatedExamType() {
        // 创建考试类型
        ExamType examType = examTypeService.createExamType(validRequest);

        // 验证结果
        assertNotNull(examType);
        assertNotNull(examType.getId(), "ID应该自动生成");
        assertEquals("期中考试", examType.getTypeName());
        // 使用 compareTo 比较 BigDecimal，忽略精度差异
        assertEquals(0, new BigDecimal("30").compareTo(examType.getRate()));
    }

    /**
     * 测试：创建考试类型 - 考试类型名称重复
     *
     * 验证：
     * 1. 第一次创建成功
     * 2. 第二次创建相同名称时抛出 IllegalArgumentException
     * 3. 异常消息包含"考试类型名称已存在"
     */
    @Test
    void createExamType_withDuplicateName_shouldThrowException() {
        // 第一次创建成功
        examTypeService.createExamType(validRequest);

        // 第二次创建相同名称，预期抛出异常
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> examTypeService.createExamType(validRequest)
        );

        // 验证异常消息
        assertTrue(exception.getMessage().contains("考试类型名称已存在"));
    }

    /**
     * 测试：创建考试类型 - 比率边界值（0和100）
     *
     * 验证：
     * 1. 比率为0时可以创建成功
     * 2. 比率为100时可以创建成功
     */
    @Test
    void createExamType_withBoundaryRate_shouldSucceed() {
        // 比率为0
        ExamTypeCreateRequest request1 = new ExamTypeCreateRequest();
        request1.setTypeName("平时成绩");
        request1.setRate(new BigDecimal("0"));
        ExamType examType1 = examTypeService.createExamType(request1);
        assertNotNull(examType1);
        assertEquals(0, new BigDecimal("0").compareTo(examType1.getRate()));

        // 比率为100
        ExamTypeCreateRequest request2 = new ExamTypeCreateRequest();
        request2.setTypeName("期末考试");
        request2.setRate(new BigDecimal("100"));
        ExamType examType2 = examTypeService.createExamType(request2);
        assertNotNull(examType2);
        assertEquals(0, new BigDecimal("100").compareTo(examType2.getRate()));
    }

    /**
     * 测试：查询考试类型 - 考试类型存在
     *
     * 验证：
     * 1. 能够正确查询到已创建的考试类型
     * 2. 查询结果与创建时一致
     */
    @Test
    void getExamType_whenExists_shouldReturnExamType() {
        // 先创建考试类型
        ExamType created = examTypeService.createExamType(validRequest);

        // 根据ID查询
        ExamType found = examTypeService.getExamTypeById(created.getId());

        // 验证查询结果
        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
        assertEquals(created.getTypeName(), found.getTypeName());
        // 使用 compareTo 比较 BigDecimal，忽略精度差异
        assertEquals(0, created.getRate().compareTo(found.getRate()));
    }

    /**
     * 测试：查询考试类型 - 考试类型不存在
     *
     * 验证：
     * 1. 查询不存在的ID时返回 null
     */
    @Test
    void getExamType_whenNotExists_shouldReturnNull() {
        // 查询不存在的ID
        ExamType found = examTypeService.getExamTypeById(999999L);

        // 验证返回 null
        assertNull(found);
    }

    /**
     * 测试：更新考试类型 - 正常情况
     *
     * 验证：
     * 1. 更新成功
     * 2. 更新后的字段正确
     */
    @Test
    void updateExamType_withValidData_shouldReturnUpdatedExamType() {
        // 先创建考试类型
        ExamType created = examTypeService.createExamType(validRequest);

        // 准备更新请求
        ExamTypeUpdateRequest updateRequest = new ExamTypeUpdateRequest();
        updateRequest.setTypeName("期末考试");
        updateRequest.setRate(new BigDecimal("50"));

        // 执行更新
        ExamType updated = examTypeService.updateExamType(created.getId(), updateRequest);

        // 验证更新结果
        assertNotNull(updated);
        assertEquals(created.getId(), updated.getId());
        assertEquals("期末考试", updated.getTypeName());
        // 使用 compareTo 比较 BigDecimal，忽略精度差异
        assertEquals(0, new BigDecimal("50").compareTo(updated.getRate()));
    }

    /**
     * 测试：更新考试类型 - 考试类型名称重复
     *
     * 验证：
     * 1. 创建两个考试类型
     * 2. 更新第二个考试类型的名称为第一个考试类型的名称时抛出异常
     */
    @Test
    void updateExamType_withDuplicateName_shouldThrowException() {
        // 创建第一个考试类型
        examTypeService.createExamType(validRequest);

        // 创建第二个考试类型
        ExamTypeCreateRequest request2 = new ExamTypeCreateRequest();
        request2.setTypeName("期末考试");
        request2.setRate(new BigDecimal("50"));
        ExamType examType2 = examTypeService.createExamType(request2);

        // 尝试将第二个考试类型的名称改为第一个考试类型的名称
        ExamTypeUpdateRequest updateRequest = new ExamTypeUpdateRequest();
        updateRequest.setTypeName("期中考试");
        updateRequest.setRate(new BigDecimal("50"));

        // 预期抛出异常
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> examTypeService.updateExamType(examType2.getId(), updateRequest)
        );

        // 验证异常消息
        assertTrue(exception.getMessage().contains("考试类型名称已存在"));
    }

    /**
     * 测试：更新考试类型 - 考试类型不存在
     *
     * 验证：
     * 1. 更新不存在的考试类型时抛出 IllegalArgumentException
     * 2. 异常消息包含"考试类型不存在"
     */
    @Test
    void updateExamType_whenNotExists_shouldThrowException() {
        // 准备更新请求
        ExamTypeUpdateRequest updateRequest = new ExamTypeUpdateRequest();
        updateRequest.setTypeName("期末考试");
        updateRequest.setRate(new BigDecimal("50"));

        // 更新不存在的考试类型，预期抛出异常
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> examTypeService.updateExamType(999999L, updateRequest)
        );

        // 验证异常消息
        assertTrue(exception.getMessage().contains("考试类型不存在"));
    }

    /**
     * 测试：删除考试类型 - 正常情况（无成绩引用）
     *
     * 验证：
     * 1. 删除成功
     * 2. 删除后查询不到该考试类型
     */
    @Test
    void deleteExamType_withoutScores_shouldSucceed() {
        // 先创建考试类型
        ExamType created = examTypeService.createExamType(validRequest);

        // 执行删除
        examTypeService.deleteExamType(created.getId());

        // 验证删除成功：查询不到该考试类型
        ExamType found = examTypeService.getExamTypeById(created.getId());
        assertNull(found, "删除后应该查询不到该考试类型");
    }

    /**
     * 测试：删除考试类型 - 考试类型不存在
     *
     * 验证：
     * 1. 删除不存在的考试类型时抛出 IllegalArgumentException
     * 2. 异常消息包含"考试类型不存在"
     */
    @Test
    void deleteExamType_whenNotExists_shouldThrowException() {
        // 删除不存在的考试类型，预期抛出异常
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> examTypeService.deleteExamType(999999L)
        );

        // 验证异常消息
        assertTrue(exception.getMessage().contains("考试类型不存在"));
    }

    /**
     * 测试：查询所有考试类型 - 按名称排序
     *
     * 验证：
     * 1. 返回所有考试类型
     * 2. 按考试类型名称升序排序
     */
    @Test
    void getAllExamTypes_shouldReturnSortedList() {
        // 创建多个考试类型（故意打乱顺序）
        ExamTypeCreateRequest request1 = new ExamTypeCreateRequest();
        request1.setTypeName("期中考试");
        request1.setRate(new BigDecimal("30"));
        examTypeService.createExamType(request1);

        ExamTypeCreateRequest request2 = new ExamTypeCreateRequest();
        request2.setTypeName("平时成绩");
        request2.setRate(new BigDecimal("20"));
        examTypeService.createExamType(request2);

        ExamTypeCreateRequest request3 = new ExamTypeCreateRequest();
        request3.setTypeName("期末考试");
        request3.setRate(new BigDecimal("50"));
        examTypeService.createExamType(request3);

        // 查询所有考试类型
        List<ExamType> examTypes = examTypeService.getAllExamTypes();

        // 验证结果
        assertNotNull(examTypes);
        assertEquals(3, examTypes.size());

        // 验证按名称升序排序：平时成绩 < 期中考试 < 期末考试
        assertEquals("平时成绩", examTypes.get(0).getTypeName());
        assertEquals("期中考试", examTypes.get(1).getTypeName());
        assertEquals("期末考试", examTypes.get(2).getTypeName());
    }
}

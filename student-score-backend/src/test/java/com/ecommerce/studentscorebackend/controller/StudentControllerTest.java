package com.ecommerce.studentscorebackend.controller;

import com.ecommerce.studentscorebackend.dto.StudentCreateRequest;
import com.ecommerce.studentscorebackend.entity.Student;
import com.ecommerce.studentscorebackend.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 学生控制器集成测试
 *
 * 测试范围：
 * 1. 创建学生 - 正常情况
 * 2. 创建学生 - 学号重复冲突
 * 3. 查询学生 - 存在
 * 4. 查询学生 - 不存在
 *
 * 使用 SpringBootTest 进行集成测试
 * 每个测试方法都在事务中运行，测试后自动回滚
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class StudentControllerTest {

    @Autowired
    private StudentService studentService;

    private StudentCreateRequest validRequest;

    /**
     * 每个测试前初始化测试数据
     */
    @BeforeEach
    void setUp() {
        // 准备有效的创建请求
        validRequest = new StudentCreateRequest();
        validRequest.setStudentNo("2024001");
        validRequest.setName("张三");
        validRequest.setGender("男");
        validRequest.setClassName("一年级1班");
        validRequest.setRemarks("测试学生");
    }

    /**
     * 测试：创建学生 - 正常情况
     *
     * 验证：
     * 1. 创建成功返回学生实体
     * 2. ID 自动生成
     * 3. 学号、姓名、班级等字段正确
     */
    @Test
    void createStudent_withValidData_shouldReturnCreatedStudent() {
        // 创建学生
        Student student = studentService.createStudent(validRequest);

        // 验证结果
        assertNotNull(student);
        assertNotNull(student.getId(), "ID应该自动生成");
        assertEquals("2024001", student.getStudentNo());
        assertEquals("张三", student.getName());
        assertEquals("男", student.getGender());
        assertEquals("一年级1班", student.getClassName());
        assertEquals("测试学生", student.getRemarks());
    }

    /**
     * 测试：创建学生 - 学号去除首尾空格
     *
     * 验证：
     * 1. 学号首尾空格被正确去除
     * 2. 创建成功
     */
    @Test
    void createStudent_shouldTrimStudentNo() {
        // 学号包含首尾空格
        validRequest.setStudentNo("  2024002  ");

        // 创建学生
        Student student = studentService.createStudent(validRequest);

        // 验证学号已去除空格
        assertEquals("2024002", student.getStudentNo());
    }

    /**
     * 测试：创建学生 - 学号重复（业务冲突）
     *
     * 验证：
     * 1. 第一次创建成功
     * 2. 第二次创建相同学号时抛出 IllegalArgumentException
     * 3. 异常消息包含"学号已存在"
     */
    @Test
    void createStudent_withDuplicateStudentNo_shouldThrowException() {
        // 第一次创建成功
        studentService.createStudent(validRequest);

        // 第二次创建相同学号，预期抛出异常
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> studentService.createStudent(validRequest)
        );

        // 验证异常消息
        assertTrue(exception.getMessage().contains("学号已存在"));
    }

    /**
     * 测试：查询学生 - 学生存在
     *
     * 验证：
     * 1. 能够正确查询到已创建的学生
     * 2. 查询结果与创建时一致
     */
    @Test
    void getStudent_whenExists_shouldReturnStudent() {
        // 先创建学生
        Student created = studentService.createStudent(validRequest);

        // 根据ID查询
        Student found = studentService.getStudentById(created.getId());

        // 验证查询结果
        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
        assertEquals(created.getStudentNo(), found.getStudentNo());
        assertEquals(created.getName(), found.getName());
    }

    /**
     * 测试：查询学生 - 学生不存在
     *
     * 验证：
     * 1. 查询不存在的ID时返回 null
     */
    @Test
    void getStudent_whenNotExists_shouldReturnNull() {
        // 查询不存在的ID
        Student found = studentService.getStudentById(999999L);

        // 验证返回 null
        assertNull(found);
    }
}

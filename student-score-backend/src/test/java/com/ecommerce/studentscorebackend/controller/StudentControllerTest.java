package com.ecommerce.studentscorebackend.controller;

import com.ecommerce.studentscorebackend.dto.PageResponse;
import com.ecommerce.studentscorebackend.dto.StudentCreateRequest;
import com.ecommerce.studentscorebackend.dto.StudentQueryRequest;
import com.ecommerce.studentscorebackend.dto.StudentUpdateRequest;
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
 * 5. 更新学生 - 正常情况
 * 6. 更新学生 - 学生不存在
 * 7. 删除学生 - 正常情况
 * 8. 删除学生 - 学生不存在
 * 9. 查询学生列表 - 无条件分页
 * 10. 查询学生列表 - 学号搜索
 * 11. 查询学生列表 - 姓名搜索
 * 12. 查询学生列表 - 班级筛选
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

    /**
     * 测试：更新学生 - 正常情况
     *
     * 验证：
     * 1. 更新成功
     * 2. 更新后的字段正确
     * 3. 学号不变
     */
    @Test
    void updateStudent_withValidData_shouldReturnUpdatedStudent() {
        // 先创建学生
        Student created = studentService.createStudent(validRequest);
        String originalStudentNo = created.getStudentNo();

        // 准备更新请求
        StudentUpdateRequest updateRequest = new StudentUpdateRequest();
        updateRequest.setName("李四");
        updateRequest.setGender("女");
        updateRequest.setClassName("一年级2班");
        updateRequest.setRemarks("更新后的备注");

        // 执行更新
        Student updated = studentService.updateStudent(created.getId(), updateRequest);

        // 验证更新结果
        assertNotNull(updated);
        assertEquals(created.getId(), updated.getId());
        assertEquals(originalStudentNo, updated.getStudentNo(), "学号不应该被修改");
        assertEquals("李四", updated.getName());
        assertEquals("女", updated.getGender());
        assertEquals("一年级2班", updated.getClassName());
        assertEquals("更新后的备注", updated.getRemarks());
    }

    /**
     * 测试：更新学生 - 学生不存在
     *
     * 验证：
     * 1. 更新不存在的学生时抛出 IllegalArgumentException
     * 2. 异常消息包含"学生不存在"
     */
    @Test
    void updateStudent_whenNotExists_shouldThrowException() {
        // 准备更新请求
        StudentUpdateRequest updateRequest = new StudentUpdateRequest();
        updateRequest.setName("李四");
        updateRequest.setClassName("一年级2班");

        // 更新不存在的学生，预期抛出异常
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> studentService.updateStudent(999999L, updateRequest)
        );

        // 验证异常消息
        assertTrue(exception.getMessage().contains("学生不存在"));
    }

    /**
     * 测试：删除学生 - 正常情况（无成绩）
     *
     * 验证：
     * 1. 删除成功
     * 2. 删除后查询不到该学生
     */
    @Test
    void deleteStudent_withoutScores_shouldSucceed() {
        // 先创建学生
        Student created = studentService.createStudent(validRequest);

        // 执行删除（不级联删除）
        studentService.deleteStudent(created.getId(), false);

        // 验证删除成功：查询不到该学生
        Student found = studentService.getStudentById(created.getId());
        assertNull(found, "删除后应该查询不到该学生");
    }

    /**
     * 测试：删除学生 - 学生不存在
     *
     * 验证：
     * 1. 删除不存在的学生时抛出 IllegalArgumentException
     * 2. 异常消息包含"学生不存在"
     */
    @Test
    void deleteStudent_whenNotExists_shouldThrowException() {
        // 删除不存在的学生，预期抛出异常
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> studentService.deleteStudent(999999L, false)
        );

        // 验证异常消息
        assertTrue(exception.getMessage().contains("学生不存在"));
    }

    /**
     * 测试：查询学生列表 - 无条件分页
     *
     * 验证：
     * 1. 返回分页结果
     * 2. 总记录数正确
     * 3. 分页参数正确
     */
    @Test
    void queryStudents_withoutConditions_shouldReturnPagedResults() {
        // 创建3个测试学生
        for (int i = 1; i <= 3; i++) {
            StudentCreateRequest request = new StudentCreateRequest();
            request.setStudentNo("202400" + i);
            request.setName("学生" + i);
            request.setClassName("一年级1班");
            studentService.createStudent(request);
        }

        // 查询第1页，每页2条
        StudentQueryRequest queryRequest = new StudentQueryRequest();
        queryRequest.setPage(1);
        queryRequest.setPageSize(2);

        PageResponse<Student> result = studentService.queryStudents(queryRequest);

        // 验证分页结果
        assertNotNull(result);
        assertEquals(3, result.getTotal(), "总记录数应该是3");
        assertEquals(2, result.getRecords().size(), "当前页应该有2条记录");
        assertEquals(1, result.getPage(), "当前页码应该是1");
        assertEquals(2, result.getPageSize(), "每页记录数应该是2");
        assertEquals(2, result.getTotalPages(), "总页数应该是2");
    }

    /**
     * 测试：查询学生列表 - 学号搜索（前缀匹配）
     *
     * 验证：
     * 1. 返回匹配的学生
     * 2. 不匹配的学生不在结果中
     */
    @Test
    void queryStudents_byStudentNo_shouldReturnMatchedResults() {
        // 创建测试数据
        StudentCreateRequest req1 = new StudentCreateRequest();
        req1.setStudentNo("2024001");
        req1.setName("张三");
        req1.setClassName("一年级1班");
        studentService.createStudent(req1);

        StudentCreateRequest req2 = new StudentCreateRequest();
        req2.setStudentNo("2024002");
        req2.setName("李四");
        req2.setClassName("一年级1班");
        studentService.createStudent(req2);

        StudentCreateRequest req3 = new StudentCreateRequest();
        req3.setStudentNo("2025001");
        req3.setName("王五");
        req3.setClassName("一年级1班");
        studentService.createStudent(req3);

        // 按学号前缀"2024"查询
        StudentQueryRequest queryRequest = new StudentQueryRequest();
        queryRequest.setStudentNo("2024");

        PageResponse<Student> result = studentService.queryStudents(queryRequest);

        // 验证结果：应该返回2个学生（2024001和2024002）
        assertNotNull(result);
        assertEquals(2, result.getTotal());
        assertTrue(result.getRecords().stream().allMatch(s -> s.getStudentNo().startsWith("2024")));
    }

    /**
     * 测试：查询学生列表 - 姓名搜索（包含匹配）
     *
     * 验证：
     * 1. 返回姓名包含关键字的学生
     */
    @Test
    void queryStudents_byName_shouldReturnMatchedResults() {
        // 创建测试数据
        StudentCreateRequest req1 = new StudentCreateRequest();
        req1.setStudentNo("2024001");
        req1.setName("张三");
        req1.setClassName("一年级1班");
        studentService.createStudent(req1);

        StudentCreateRequest req2 = new StudentCreateRequest();
        req2.setStudentNo("2024002");
        req2.setName("张四");
        req2.setClassName("一年级1班");
        studentService.createStudent(req2);

        StudentCreateRequest req3 = new StudentCreateRequest();
        req3.setStudentNo("2024003");
        req3.setName("李五");
        req3.setClassName("一年级1班");
        studentService.createStudent(req3);

        // 按姓名"张"查询
        StudentQueryRequest queryRequest = new StudentQueryRequest();
        queryRequest.setName("张");

        PageResponse<Student> result = studentService.queryStudents(queryRequest);

        // 验证结果：应该返回2个学生（张三和张四）
        assertNotNull(result);
        assertEquals(2, result.getTotal());
        assertTrue(result.getRecords().stream().allMatch(s -> s.getName().contains("张")));
    }

    /**
     * 测试：查询学生列表 - 班级筛选（精确匹配）
     *
     * 验证：
     * 1. 返回指定班级的学生
     * 2. 其他班级的学生不在结果中
     */
    @Test
    void queryStudents_byClassName_shouldReturnMatchedResults() {
        // 创建测试数据
        StudentCreateRequest req1 = new StudentCreateRequest();
        req1.setStudentNo("2024001");
        req1.setName("张三");
        req1.setClassName("一年级1班");
        studentService.createStudent(req1);

        StudentCreateRequest req2 = new StudentCreateRequest();
        req2.setStudentNo("2024002");
        req2.setName("李四");
        req2.setClassName("一年级1班");
        studentService.createStudent(req2);

        StudentCreateRequest req3 = new StudentCreateRequest();
        req3.setStudentNo("2024003");
        req3.setName("王五");
        req3.setClassName("一年级2班");
        studentService.createStudent(req3);

        // 按班级"一年级1班"查询
        StudentQueryRequest queryRequest = new StudentQueryRequest();
        queryRequest.setClassName("一年级1班");

        PageResponse<Student> result = studentService.queryStudents(queryRequest);

        // 验证结果：应该返回2个学生（张三和李四）
        assertNotNull(result);
        assertEquals(2, result.getTotal());
        assertTrue(result.getRecords().stream().allMatch(s -> s.getClassName().equals("一年级1班")));
    }
}

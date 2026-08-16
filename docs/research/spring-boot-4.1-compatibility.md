# Spring Boot 4.1.0 兼容配置核对

核对日期：2026-08-16  
范围：学生成绩处理系统后端（Java、Maven、Spring Web MVC、Validation、Actuator、测试、MyBatis-Plus、Flyway、MySQL Connector/J）  
取证原则：仅采用项目官方文档、官方源码/发布信息、Spring Initializr 和 Maven Central 发布 POM/metadata。

## 结论摘要

1. `Spring Boot 4.1.0` 是稳定版本；Maven 父 POM 中应写 `4.1.0`，不要写 `4.1.0.RELEASE`。
2. 最低运行环境为 Java 17；Spring Boot 官方支持 Maven 3.6.3 及以上。现有 JDK 17 可继续使用。
3. Boot 4 的 Servlet Web starter 是 `spring-boot-starter-webmvc`，不是旧项目常见的 `spring-boot-starter-web`。
4. 通用测试 starter `spring-boot-starter-test` 在 Boot 4.1 中仍然存在；但对本项目，官方 Initializr 会生成更具体的 `spring-boot-starter-webmvc-test`，选择 Actuator 和 Validation 时还会生成各自的 `spring-boot-starter-actuator-test`、`spring-boot-starter-validation-test`。这些功能测试 starter 会传递引入通用测试 starter，不要重复声明。
5. MyBatis-Plus 应使用 Boot 4 专用 starter：`com.baomidou:mybatis-plus-spring-boot4-starter:3.5.17`。Spring Boot 4.1.0 的 BOM 不管理 MyBatis-Plus，因此必须显式写版本。
6. MySQL 下的 Flyway 配置应同时包含 `spring-boot-starter-flyway` 与 `org.flywaydb:flyway-mysql`。
7. MySQL JDBC 驱动坐标是 `com.mysql:mysql-connector-j`，建议使用 `runtime` scope。
8. 采用 `spring-boot-starter-parent:4.1.0` 后，Spring Boot starters、Flyway 和 MySQL Connector/J 都不应手工写版本；由 Boot BOM 统一管理。

## 1. Java 与 Maven 要求

Spring Boot 4.1.0 官方系统要求页明确写明：

- Java：至少 Java 17，最高兼容到 Java 26。
- Maven：3.6.3 或更高版本。
- Spring Framework：7.0.8 或更高版本。

因此本项目可固定为：

```text
Java: 17
Maven: 3.6.3+
Spring Boot: 4.1.0
Packaging: Jar
```

来源：

- [Spring Boot 4.1.0 - System Requirements](https://docs.spring.io/spring-boot/4.1/system-requirements.html)
- [Spring Boot starter parent 4.1.0 POM（Maven Central）](https://repo1.maven.org/maven2/org/springframework/boot/spring-boot-starter-parent/4.1.0/spring-boot-starter-parent-4.1.0.pom)

## 2. Spring Boot 4 starter 名称

对 `web,validation,actuator` 依赖组合，Spring 官方 Initializr 的 Boot 4.1.0 Maven POM 生成以下编译依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webmvc</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

对应的测试依赖是：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webmvc-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator-test</artifactId>
    <scope>test</scope>
</dependency>
```

`org.springframework.boot:spring-boot-starter-test` 仍是 Boot 4.1 的通用测试 starter。上面三个功能测试 starter 都会传递引入它，因此本项目会使用 Spring MVC/MockMvc 时，至少保留 `spring-boot-starter-webmvc-test`，无需再直接重复声明 `spring-boot-starter-test`。若项目由 IDEA/Initializr 自动生成了另外两个功能测试 starter，也应保留；它们是 Boot 4 模块化后的官方生成结果。

来源：

- [Spring Initializr metadata](https://start.spring.io/metadata/client)
- [Spring Initializr Boot 4.1.0 官方生成 POM：Web、Validation、Actuator](https://start.spring.io/pom.xml?type=maven-build&language=java&bootVersion=4.1.0&groupId=com.example&artifactId=demo&name=demo&packageName=com.example.demo&packaging=jar&javaVersion=17&dependencies=web%2Cvalidation%2Cactuator)
- [Spring Boot 4.1 starter 与测试 starter 坐标清单](https://docs.spring.io/spring-boot/4.1/appendix/dependency-versions/coordinates.html)

## 3. MyBatis-Plus 的 Boot 4 配置

MyBatis-Plus 已发布 Boot 4 专用 starter。Maven 坐标应为：

```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot4-starter</artifactId>
    <version>3.5.17</version>
</dependency>
```

核对结果：

- Maven Central metadata 显示 `3.5.17` 是当前 `latest` 和 `release`。
- 3.5.17 的发布 POM 明确依赖 `mybatis-spring:4.0.0`、MyBatis-Plus 自动配置模块和 `spring-boot-starter-jdbc`。
- Spring Boot 4.1.0 BOM 中没有 MyBatis-Plus 条目，因此 `3.5.17` 不能省略。
- 不应再添加 `mybatis-spring-boot-starter` 或 Initializr 中的普通 MyBatis starter，以免引入另一套自动配置。

来源：

- [MyBatis-Plus 官方安装文档](https://baomidou.com/getting-started/install/)
- [MyBatis-Plus Boot 4 starter Maven metadata](https://repo1.maven.org/maven2/com/baomidou/mybatis-plus-spring-boot4-starter/maven-metadata.xml)
- [MyBatis-Plus Boot 4 starter 3.5.17 发布 POM](https://repo1.maven.org/maven2/com/baomidou/mybatis-plus-spring-boot4-starter/3.5.17/mybatis-plus-spring-boot4-starter-3.5.17.pom)
- [MyBatis-Plus v3.5.17 官方发布记录](https://github.com/baomidou/mybatis-plus/releases/tag/v3.5.17)

## 4. Flyway 与 MySQL

Spring Initializr 对 Boot 4.1.0 的 `mysql,flyway` 组合生成：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-flyway</artifactId>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-mysql</artifactId>
</dependency>
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

这意味着 Boot 4 项目不要只添加旧式的 `flyway-core`：Spring Boot 的 Flyway starter 负责 Boot 集成，MySQL 数据库支持由 `flyway-mysql` 模块提供。

来源：

- [Spring Initializr Boot 4.1.0 官方生成 POM：MySQL、Flyway](https://start.spring.io/pom.xml?type=maven-build&language=java&bootVersion=4.1.0&groupId=com.example&artifactId=demo&name=demo&packageName=com.example.demo&packaging=jar&javaVersion=17&dependencies=mysql%2Cflyway)
- [MySQL Connector/J 官方 Maven 安装说明](https://dev.mysql.com/doc/connector-j/en/connector-j-installing-maven.html)
- [Spring Boot 4.1.0 dependency BOM POM](https://repo1.maven.org/maven2/org/springframework/boot/spring-boot-dependencies/4.1.0/spring-boot-dependencies-4.1.0.pom)

## 5. 版本管理边界

Spring Boot 4.1.0 BOM 的实际管理结果如下：

| 依赖 | Boot 4.1.0 BOM 管理版本 | 项目 POM 是否写版本 |
|---|---:|---|
| `org.springframework.boot:*` starters | `4.1.0` | 否 |
| `org.flywaydb:flyway-mysql` / Flyway | `12.4.0` | 否 |
| `com.mysql:mysql-connector-j` | `9.7.0` | 否 |
| `com.baomidou:mybatis-plus-spring-boot4-starter` | 不在 Boot BOM 中 | 是，写 `3.5.17` |

推荐父 POM：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>4.1.0</version>
    <relativePath/>
</parent>

<properties>
    <java.version>17</java.version>
    <mybatis-plus.version>3.5.17</mybatis-plus.version>
</properties>
```

由此，PRD/配置文档中不应把 Flyway、Connector/J、JUnit、Mockito 等 Boot 管理的传递依赖版本写死。只记录由项目主动选择、且不在 Boot BOM 中的 MyBatis-Plus 版本。

来源：

- [Spring Boot 4.1.0 dependency BOM POM](https://repo1.maven.org/maven2/org/springframework/boot/spring-boot-dependencies/4.1.0/spring-boot-dependencies-4.1.0.pom)
- [Spring Boot Maven Plugin - Using the Plugin](https://docs.spring.io/spring-boot/4.1/maven-plugin/using.html)

## 建议用于正式 PRD 的技术基线

```text
Java 17
Maven 3.6.3+
Spring Boot 4.1.0
Spring Framework 7.0.8+
Spring Web MVC（spring-boot-starter-webmvc）
Jakarta Validation（spring-boot-starter-validation）
Spring Boot Actuator（spring-boot-starter-actuator）
Spring MVC Test（spring-boot-starter-webmvc-test）
MyBatis-Plus 3.5.17（mybatis-plus-spring-boot4-starter）
Flyway 12.4.0（由 Boot BOM 管理；starter + flyway-mysql）
MySQL Connector/J 9.7.0（由 Boot BOM 管理）
MySQL Server 8.0
```

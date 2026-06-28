# 图书管理系统课程设计报告

## 摘要

本项目为 Java 课程设计作业，旨在开发一个基于 Java Swing 的桌面图书管理系统。系统采用 Maven 构建，H2 嵌入式数据库实现数据持久化，使用 JDBC 进行数据库访问。系统实现了用户登录与权限控制、图书信息管理、图书销售、销售记录、库存预警、数据导入导出等功能，并采用左侧导航栏的现代化界面布局。通过单元测试与运行验证，系统功能完整、运行稳定。

**关键词**：图书管理系统；Java Swing；H2 数据库；JDBC；三层架构

---

## 目录

1. [引言](#一引言)
2. [系统功能分析](#二系统功能分析)
3. [设计思路](#三设计思路)
4. [系统设计](#四系统设计)
5. [系统实现与测试](#五系统实现与测试)
6. [结论与心得体会](#六结论与心得体会)
7. [参考文献](#七参考文献)

---

## 一、引言

### 1.1 项目背景

随着图书销售行业的发展，传统的手工记录方式已经无法满足日益增长的管理需求。图书管理系统可以有效地管理图书库存、销售记录，提高工作效率，减少人为错误。

### 1.2 项目目标

本项目目标是开发一个功能完善、界面友好的桌面图书管理系统，主要实现以下目标：

- 实现图书信息的增删改查；
- 实现图书销售与库存自动扣减；
- 实现销售记录管理；
- 实现用户登录与权限控制；
- 实现数据导入导出功能；
- 实现销售统计报表功能（柱状图、饼图）；
- 提供良好的图形化用户界面。

### 1.3 技术选型

| 技术/工具 | 选型 | 说明 |
|-----------|------|------|
| 编程语言 | Java 17 | 现代 Java 特性支持 |
| 构建工具 | Maven | 依赖管理与项目构建 |
| GUI 框架 | Swing | JDK 内置，跨平台 |
| 数据库 | H2 Database | 嵌入式数据库，零安装 |
| 数据库访问 | JDBC | 原生 SQL，便于学习 |
| 金额计算 | BigDecimal | 避免浮点数精度问题 |
| 导入导出 | Apache POI / OpenCSV | Excel 与 CSV 文件处理 |
| 图表库 | JFreeChart | 柱状图、饼图生成 |
| 测试框架 | JUnit 5 | 单元测试 |

---

## 二、系统功能分析

### 2.1 功能模块划分

系统按用户角色划分为管理员功能和店员功能：

#### 2.1.1 管理员功能

- **数据概览**：支持按书名、ISBN、作者、出版社进行多条件模糊查询；支持将当前列表导出为 Excel；支持从 Excel/CSV 导入图书数据；展示图书总数与当前展示数量；提供销售统计图表（按日/月/年统计销售额与销量，使用 JFreeChart 生成柱状图和饼图）；数据概览内使用子标签页切换书籍列表与销售统计，保证每个模块有充足展示空间。
- **库存管理**：支持图书的添加、修改、删除；支持多条件搜索定位；双击表格行可回填表单；库存与价格输入框限制数字输入。
- **图书销售**：输入 ISBN 与购买数量进行销售，系统自动扣减库存并计算应收金额。
- **销售记录**：查看历史销售明细，展示销售笔数与销售总额。

#### 2.1.2 店员功能

- **图书销售**：输入 ISBN 与购买数量进行销售。
- **销售记录**：查看历史销售明细。

### 2.2 非功能需求

- **数据持久化**：使用 H2 嵌入式数据库，程序重启后数据不丢失。
- **库存预警**：库存量低于 10 的图书在表格中以红色高亮显示。
- **权限控制**：管理员与店员登录后看到不同的功能菜单。
- **界面友好**：采用左侧导航栏布局，界面美观，操作反馈明确。

---

## 三、设计思路

### 3.1 架构设计

系统采用经典的三层架构，实现表现层、业务逻辑层、数据访问层的分离：

```
┌─────────────────────────────────────┐
│  表现层（Presentation Layer）        │
│  Swing GUI：登录框、主窗口、数据概览 │
│  面板、库存管理面板、销售面板等       │
├─────────────────────────────────────┤
│  业务逻辑层（Service Layer）         │
│  BookService、SaleService、UserService│
│  负责业务规则处理                     │
├─────────────────────────────────────┤
│  数据访问层（Data Access Layer）     │
│  BookRepository、SaleRepository、    │
│  UserRepository 及 JDBC 实现          │
├─────────────────────────────────────┤
│  实体层（Entity Layer）              │
│  Book、Sale、User                    │
└─────────────────────────────────────┘
```

### 3.2 解耦设计

- 业务层依赖数据访问接口，不依赖具体实现，便于单元测试时替换为内存实现。
- 表现层通过 Service 接口与业务层交互，不直接操作数据库。
- 实体层独立于其他层，便于复用和维护。

### 3.3 界面设计

系统采用左侧导航栏 + 右侧内容区的布局：

```
┌─────────────────────────────────────────────┐
│  图书管理系统        当前用户: admin / 管理员  │
├───────────────┬─────────────────────────────┤
│   数据概览     │                             │
│   库存管理     │      右侧主内容区            │
│   图书销售     │      （CardLayout 切换）     │
│   销售记录     │                             │
└───────────────┴─────────────────────────────┘
```

### 3.4 数据库设计

#### 图书表（books）

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| isbn | VARCHAR(20) | PRIMARY KEY | 国际标准书号 |
| title | VARCHAR(100) | NOT NULL | 书名 |
| publisher | VARCHAR(100) | | 出版社 |
| author | VARCHAR(100) | | 作者 |
| stock | INT | NOT NULL CHECK >= 0 | 库存量 |
| price | DECIMAL(10,2) | NOT NULL CHECK >= 0 | 单价 |

#### 销售记录表（sales）

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | AUTO_INCREMENT PRIMARY KEY | 销售记录 ID |
| isbn | VARCHAR(20) | NOT NULL | ISBN |
| title | VARCHAR(100) | NOT NULL | 书名 |
| quantity | INT | NOT NULL CHECK > 0 | 销售数量 |
| amount | DECIMAL(10,2) | NOT NULL | 销售金额 |
| sale_time | TIMESTAMP | NOT NULL | 销售时间 |

#### 用户表（users）

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| username | VARCHAR(50) | PRIMARY KEY | 用户名 |
| password | VARCHAR(100) | NOT NULL | 密码 |
| role | VARCHAR(20) | NOT NULL | 角色（ADMIN/CLERK） |

---

## 四、系统设计

### 4.1 包结构

```
src/main/java/com/bms/
├── App.java                    # 程序入口
├── entity/
│   ├── Book.java               # 图书实体
│   ├── Sale.java               # 销售记录实体
│   └── User.java               # 用户实体
├── repository/
│   ├── BookRepository.java     # 图书数据访问接口
│   ├── JdbcBookRepository.java # 图书 JDBC 实现
│   ├── SaleRepository.java     # 销售记录数据访问接口
│   ├── JdbcSaleRepository.java # 销售记录 JDBC 实现
│   ├── UserRepository.java     # 用户数据访问接口
│   └── JdbcUserRepository.java # 用户 JDBC 实现
├── service/
│   ├── BookService.java        # 图书业务逻辑
│   ├── SaleService.java        # 销售记录业务逻辑
│   └── UserService.java        # 用户业务逻辑
├── ui/
│   ├── MainFrame.java          # 主窗口
│   ├── LoginDialog.java        # 登录对话框
│   ├── DataOverviewPanel.java  # 数据概览面板
│   ├── InventoryPanel.java     # 库存管理面板
│   ├── SalesPanel.java         # 图书销售面板
│   ├── SalesHistoryPanel.java  # 销售记录面板
│   ├── BookTableModel.java     # 图书表格模型
│   ├── StockWarningRenderer.java # 库存预警渲染器
│   └── ...
└── util/
    ├── DatabaseUtil.java       # 数据库工具
    └── DataInitializer.java    # 演示数据初始化
```

### 4.2 关键类设计

#### BookService

负责图书相关业务逻辑，包括添加、修改、删除、查询、销售等。销售时校验库存，扣减库存并记录销售。

#### SaleService

负责销售记录管理，包括记录销售、查询全部销售记录、统计销售笔数和总额。

#### UserService

负责用户登录验证和默认账号初始化。

### 4.3 交互流程

以图书销售为例：

1. 用户在销售面板输入 ISBN 和购买数量；
2. 点击"确认销售"后调用 `BookService.sellBook()`；
3. `BookService` 查询图书、校验库存、计算金额；
4. 扣减库存并更新数据库；
5. 调用 `SaleService.recordSale()` 记录销售；
6. 返回销售成功提示。

---

## 五、系统实现与测试

### 5.1 核心功能实现

#### 5.1.1 用户登录与权限

系统启动时显示登录对话框，验证用户名和密码。管理员和店员登录后，左侧导航栏显示不同的功能入口。

默认账号：

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin | 管理员 |
| clerk | clerk | 店员 |

#### 5.1.2 图书管理

库存管理面板支持图书的添加、修改、删除。添加和修改时校验 ISBN 唯一性、必填项、价格和库存非负。删除前进行二次确认。

#### 5.1.3 图书销售

销售时精确计算金额，使用 `BigDecimal` 避免浮点误差。库存不足时给出明确提示。

#### 5.1.4 库存预警

通过自定义 `StockWarningRenderer`，当库存低于 10 时，表格行显示为浅红背景 + 红色文字。

#### 5.1.5 数据导入导出

使用 Apache POI 实现 Excel 导入导出，使用 OpenCSV 实现 CSV 导入。

#### 5.1.6 统计报表

系统提供销售统计报表功能，支持按日、按月、按年统计销售额与销量。数据访问层使用 SQL 的 `FORMATDATETIME` 函数按时间维度聚合，业务层返回统计结果，表现层使用 JFreeChart 生成柱状图（销售额）和饼图（销量占比），集成在数据概览界面中展示。

### 5.2 测试

项目使用 JUnit 5 编写了单元测试，覆盖核心业务逻辑。

测试文件：

- `BookServiceTest.java`：测试图书添加、ISBN 唯一性、销售扣减库存、库存不足、模糊查询等。
- `SaleServiceTest.java`：测试销售记录保存、查询、统计。
- `UserServiceTest.java`：测试用户登录验证。

测试结果：

```bash
mvn clean test
```

所有测试用例全部通过。

### 5.3 运行验证

通过以下命令打包并运行：

```bash
mvn clean package
java -jar target/book-management-system-1.0-SNAPSHOT.jar
```

系统启动正常，登录框、导航栏、各功能面板均能正确显示和操作。

---

## 六、结论与心得体会

### 6.1 结论

本项目成功实现了一个功能较为完整的图书管理系统，具备以下特点：

1. **功能完整**：涵盖图书管理、销售、记录、权限、数据导入导出等核心功能。
2. **架构清晰**：采用三层架构，层与层之间通过接口解耦。
3. **界面友好**：采用左侧导航栏布局，界面美观，操作便捷。
4. **数据持久化**：使用 H2 数据库，数据可持久保存。
5. **测试充分**：通过单元测试保证核心功能正确性。

### 6.2 心得体会

通过本次课程设计，我对 Java 桌面应用开发有了更深入的理解：

1. **Swing 界面开发**：掌握了 Swing 的布局管理器、事件处理、自定义渲染等知识。
2. **数据库访问**：通过 JDBC 熟悉了数据库连接、CRUD 操作、事务处理。
3. **架构设计**：认识到分层架构的重要性，它能提高代码的可维护性和可测试性。
4. **Maven 构建**：学会了使用 Maven 管理项目依赖和构建。
5. **测试驱动开发**：体会到编写单元测试对保证代码质量的重要作用。

在开发过程中也遇到了一些问题，例如 H2 数据库文件锁定、Swing 事件处理线程、图标资源加载等，通过查阅资料和反复调试最终解决，收获很大。

---

## 七、参考文献

[1] Cay S. Horstmann. Java 核心技术（卷 I、卷 II）[M]. 北京: 机械工业出版社, 2020.

[2] Joshua Bloch. Effective Java（原书第 3 版）[M]. 北京: 机械工业出版社, 2019.

[3] H2 Database Engine. H2 Database Documentation[EB/OL]. https://www.h2database.com/html/main.html.

[4] Apache Software Foundation. Apache POI - the Java API for Microsoft Documents[EB/OL]. https://poi.apache.org/.

[5] OpenCSV. Apache Commons CSV / OpenCSV Documentation[EB/OL]. http://opencsv.sourceforge.net/.

[6] Oracle. The Java Tutorials - Trail: Creating a GUI With Swing[EB/OL]. https://docs.oracle.com/javase/tutorial/uiswing/.

[7] JUnit Team. JUnit 5 User Guide[EB/OL]. https://junit.org/junit5/docs/current/user-guide/.

[8] Apache Maven Project. Maven Getting Started Guide[EB/OL]. https://maven.apache.org/guides/getting-started/.

---

## 附录：运行说明

### 环境要求

- JDK 17 或更高版本
- Maven 3.8 或更高版本

### 编译运行

```bash
# 编译测试
mvn clean test

# 打包
mvn clean package

# 运行
java -jar target/book-management-system-1.0-SNAPSHOT.jar
```

### 默认账号

- 管理员：`admin` / `admin`
- 店员：`clerk` / `clerk`

### 注意事项

- H2 数据库文件默认生成在 `./data/` 目录下，已加入 `.gitignore`。
- 首次启动会自动创建表结构并初始化 100 条演示数据。

# 图书管理系统

Java 课程设计：基于 Swing + H2 数据库的桌面图书管理系统。

## 功能

- 图书信息管理：添加、查询、修改、删除
- 用户登录与权限：区分管理员（全部功能）和店员（仅销售、销售记录查询）
- 库存预警：库存低于 10 的图书在表格中高亮显示
- 图书销售：按 ISBN 销售，自动扣减库存，使用 BigDecimal 精确计算金额；ISBN 框支持回车查询
- 销售记录：每次销售自动记录，支持查看历史销售明细、总笔数与总金额
- 统计分析：按价格、库存量、作者、出版社排序展示
- 数据导入导出：支持 Excel（.xlsx）和 CSV 导入，Excel 导出图书数据
- 用户体验：价格 / 库存输入框限制数字输入；双击表格行回填表单；表格列宽自适应
- 数据持久化：H2 嵌入式数据库，程序重启后数据不丢失

## 技术栈

- Java 17+
- Maven
- Swing
- H2 Database
- JDBC

## 运行方式

```bash
mvn clean package
java -jar target/book-management-system-1.0-SNAPSHOT.jar
```

## 架构

- `entity`：实体层
- `repository`：数据访问层
- `service`：业务逻辑层
- `ui`：表现层（Swing）
- `util`：工具类

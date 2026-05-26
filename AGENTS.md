# AGENTS.md

## Cursor Cloud specific instructions

### 项目概述

本项目是 **e店邦 O2O 平台 (car_o2o)** —— 一个汽车服务门店管理系统，基于 Spring Boot 2.4.3 (Java 11)、MyBatis、Apache Shiro、Activiti 7 和 Thymeleaf 构建。单体应用，非微服务/非 monorepo。

### 环境依赖

- **JDK 11** —— 项目要求 Java 11（`pom.xml` 中 `java.version=11`）。若系统安装了多个 JDK，需切换默认版本：`sudo update-alternatives --set java /usr/lib/jvm/java-11-openjdk-amd64/bin/java`
- **Maven 3.x** —— 项目未包含 Maven Wrapper（`mvnw`），需系统安装 Maven。
- **MySQL 8.x** —— 数据库名 `car_o2o`，用户 `root`，密码 `admin`，端口 3306。建表脚本位于 `sql/car_o2o.sql`。

### 重要注意事项

1. **Activiti 与 MySQL 8 外键冲突**：SQL 导入脚本（`sql/car_o2o.sql`）包含 Activiti 引擎表。当配置 `spring.activiti.database-schema-update=true`（默认值）时，Activiti 会尝试重建这些表，在 MySQL 8 上报 `Duplicate foreign key constraint name` 错误。**解决方案**：首次启动前，先删除所有 `act_*` 表，让 Activiti 自动重建：
   ```sql
   SET FOREIGN_KEY_CHECKS = 0;
   DROP TABLE IF EXISTS act_evt_log, act_ge_bytearray, act_ge_property, act_hi_actinst, act_hi_attachment, act_hi_comment, act_hi_detail, act_hi_identitylink, act_hi_procinst, act_hi_taskinst, act_hi_varinst, act_procdef_info, act_re_deployment, act_re_model, act_re_procdef, act_ru_deadletter_job, act_ru_event_subscr, act_ru_execution, act_ru_identitylink, act_ru_integration, act_ru_job, act_ru_suspended_job, act_ru_task, act_ru_timer_job, act_ru_variable;
   SET FOREIGN_KEY_CHECKS = 1;
   ```

2. **文件上传路径**：`application.yml` 中 `system.car.profile` 默认为 `D:/car/uploadPath`（Windows 路径）。在 Linux 环境下需运行时覆盖：
   ```bash
   mvn spring-boot:run -Dspring-boot.run.arguments="--system.car.profile=/home/car/uploadPath"
   ```
   需提前创建目录：`sudo mkdir -p /home/car/uploadPath && sudo chmod 777 /home/car/uploadPath`

3. **无自动化测试**：项目没有 `src/test/` 目录，没有测试类。

### 启动方式

```bash
# 启动 MySQL（如未运行）
sudo mysqld --user=mysql --datadir=/var/lib/mysql &

# 启动应用（端口 8080）
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
mvn spring-boot:run -Dspring-boot.run.arguments="--system.car.profile=/home/car/uploadPath"
```

### 编译与构建

```bash
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
mvn compile -DskipTests    # 仅编译
mvn package -DskipTests    # 构建 JAR 包
```

项目未配置独立的 linter 或代码格式化工具。

### 默认账号

- **应用登录**：`admin` / `admin123`
- **Druid 监控台**（`/druid/*`）：`wolfcode` / `admin`

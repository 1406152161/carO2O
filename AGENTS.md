# AGENTS.md

## Cursor Cloud specific instructions

### Project overview

This is the **e店邦 O2O Platform (car_o2o)** — a Car Service Shop Management System built with Spring Boot 2.4.3 (Java 11), MyBatis, Apache Shiro, Activiti 7, and Thymeleaf. It is a single monolithic application (not a monorepo).

### Prerequisites

- **JDK 11** — The project requires Java 11 (`java.version=11` in `pom.xml`). If multiple JDKs are installed, set the default with `sudo update-alternatives --set java /usr/lib/jvm/java-11-openjdk-amd64/bin/java`.
- **Maven 3.x** — No Maven wrapper (`mvnw`) is bundled; system Maven must be installed.
- **MySQL 8.x** — Database `car_o2o`, user `root`, password `admin`, port 3306. Schema at `sql/car_o2o.sql`.

### Important gotchas

1. **Activiti + MySQL 8 FK conflict**: The SQL import (`sql/car_o2o.sql`) includes Activiti engine tables. When `spring.activiti.database-schema-update=true` (the default), Activiti tries to recreate these tables, causing `Duplicate foreign key constraint name` errors on MySQL 8. **Solution**: Before first startup, drop all `act_*` tables and let Activiti recreate them:
   ```sql
   SET FOREIGN_KEY_CHECKS = 0;
   DROP TABLE IF EXISTS act_evt_log, act_ge_bytearray, act_ge_property, act_hi_actinst, act_hi_attachment, act_hi_comment, act_hi_detail, act_hi_identitylink, act_hi_procinst, act_hi_taskinst, act_hi_varinst, act_procdef_info, act_re_deployment, act_re_model, act_re_procdef, act_ru_deadletter_job, act_ru_event_subscr, act_ru_execution, act_ru_identitylink, act_ru_integration, act_ru_job, act_ru_suspended_job, act_ru_task, act_ru_timer_job, act_ru_variable;
   SET FOREIGN_KEY_CHECKS = 1;
   ```

2. **Upload path**: `application.yml` sets `system.car.profile: D:/car/uploadPath` (Windows). On Linux, override at runtime:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.arguments="--system.car.profile=/home/car/uploadPath"
   ```
   Create the directory first: `sudo mkdir -p /home/car/uploadPath && sudo chmod 777 /home/car/uploadPath`

3. **No automated tests**: The project has no `src/test/` directory and no test classes.

### How to run

```bash
# Start MySQL (if not already running)
sudo mysqld --user=mysql --datadir=/var/lib/mysql &

# Start the application (port 8080)
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
mvn spring-boot:run -Dspring-boot.run.arguments="--system.car.profile=/home/car/uploadPath"
```

### Build & lint

```bash
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
mvn compile -DskipTests    # compile only
mvn package -DskipTests    # build JAR
```

No separate linter or formatter is configured in the project.

### Default credentials

- **App login**: `admin` / `admin123`
- **Druid console** (`/druid/*`): `wolfcode` / `admin`

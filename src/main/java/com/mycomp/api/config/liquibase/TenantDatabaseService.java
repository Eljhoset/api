package com.mycomp.api.config.liquibase;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import javax.sql.DataSource;
import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.core.io.ResourceLoader;

/**
 *
 * @author Daniel
 */
public abstract class TenantDatabaseService {

    private static final String DDL_CREATE_SCHEMA = "CREATE SCHEMA IF NOT EXISTS %s";
    @Autowired
    private DataSource dataSource;
    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    private LiquibaseProperties liquibaseProperties;
    @Value("${spring.liquibase.change-log.tenant:classpath:/db/changelog/db.changelog-tenant.yaml}")
    private String changeLogPath;

    public void create(String tenant) {
        try (Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement()) {
            statement.executeUpdate(String.format(DDL_CREATE_SCHEMA, tenant));
        } catch (SQLException e) {
            throw new RuntimeException("Can not connect to database", e);
        }
    }

    public void migrate(String tenantKey) throws LiquibaseException {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setResourceLoader(resourceLoader);
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(changeLogPath);
        liquibase.setContexts(liquibaseProperties.getContexts());
        liquibase.setDefaultSchema(tenantKey);
        liquibase.setDropFirst(liquibaseProperties.isDropFirst());
        liquibase.setShouldRun(true);
        liquibase.afterPropertiesSet();
    }

    public abstract List<String> getTenants();
}

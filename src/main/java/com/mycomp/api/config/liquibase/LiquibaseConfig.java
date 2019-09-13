package com.mycomp.api.config.liquibase;

import liquibase.integration.spring.MultiTenantSpringLiquibase;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ResourceLoader;

import javax.sql.DataSource;
import java.util.List;

/**
 * @author Daniel
 */
@Configuration
@EnableConfigurationProperties(LiquibaseProperties.class)
public class LiquibaseConfig {

    @Value("${spring.liquibase.change-log.tenant:classpath:/db/changelog/db.changelog-tenant.yaml}")
    private String changeLogTenantPath;
    @Value("${spring.liquibase.change-log:classpath:/db/changelog/db.changelog-master.yaml}")
    private String changeLogPath;

    @Bean
    public SpringLiquibase liquibase(DataSource dataSource, LiquibaseProperties liquibaseProperties) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(changeLogPath);
        liquibase.setContexts(liquibaseProperties.getContexts());
        liquibase.setDefaultSchema(liquibaseProperties.getDefaultSchema());
        liquibase.setDropFirst(liquibaseProperties.isDropFirst());
        return liquibase;
    }

    @Bean
    @DependsOn("liquibase")
    public MultiTenantSpringLiquibase multiTenantLiquibase(ResourceLoader resourceLoader,
            DataSource dataSource,
            LiquibaseProperties liquibaseProperties,
            TenantDatabaseService tenantDatabaseService) {

        MultiTenantSpringLiquibase liquibase = new SchemaMultiTenantSpringLiquibase();
        liquibase.setResourceLoader(resourceLoader);
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(changeLogTenantPath);
        liquibase.setContexts(liquibaseProperties.getContexts());
        liquibase.setDropFirst(liquibaseProperties.isDropFirst());
        final List<String> schemas = tenantDatabaseService.getTenants();

        schemas.forEach(schema -> {
            tenantDatabaseService.create(schema);
            liquibase.setDefaultSchema(schema);
        });

        liquibase.setSchemas(schemas);
        liquibase.setShouldRun(liquibaseProperties.isEnabled());
        return liquibase;
    }
}

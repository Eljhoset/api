package com.mycomp.api.config.liquibase;

import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.MultiTenantSpringLiquibase;
import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.java.Log;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author Daniel
 */
@Log
public class SchemaMultiTenantSpringLiquibase extends MultiTenantSpringLiquibase {

    private ResourceLoader resourceLoader;

    public SchemaMultiTenantSpringLiquibase() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        if (getDataSource() == null && getSchemas() == null) {
            super.afterPropertiesSet();
        } else {
            if (getDataSource() == null && getSchemas() != null) {
                throw new LiquibaseException("When schemas are defined you should also define a base dataSource");
            }

            if (getDataSource() != null) {
                if (CollectionUtils.isEmpty(getSchemas())) {
                    setSchemas(new ArrayList<>());
                    getSchemas().add(getDefaultSchema());
                }

                runOnAllSchemas();
            }
        }
    }

    private void runOnAllSchemas() throws LiquibaseException {

        for (String schema : getSchemas()) {
            if (schema == null)
                continue;

            SpringLiquibase liquibase = getSpringLiquibase(getDataSource());
            liquibase.setDefaultSchema(schema);
            liquibase.setChangeLogParameters(Map.of("schema", schema));
            liquibase.afterPropertiesSet();
        }

    }

    private SpringLiquibase getSpringLiquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setChangeLog(getChangeLog());
        liquibase.setChangeLogParameters(getParameters());
        liquibase.setContexts(getContexts());
        liquibase.setLabels(getLabels());
        liquibase.setDropFirst(isDropFirst());
        liquibase.setShouldRun(isShouldRun());
        liquibase.setRollbackFile(getRollbackFile());
        liquibase.setResourceLoader(getResourceLoader());
        liquibase.setDataSource(dataSource);
        liquibase.setDefaultSchema(getDefaultSchema());
        return liquibase;
    }

    private ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        super.setResourceLoader(resourceLoader);
        this.resourceLoader = resourceLoader;

    }
}

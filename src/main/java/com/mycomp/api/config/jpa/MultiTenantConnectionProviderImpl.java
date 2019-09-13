package com.mycomp.api.config.jpa;

import com.mycomp.api.config.TenantContext;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.hibernate.HibernateException;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author Daniel
 */
@Component
public class MultiTenantConnectionProviderImpl implements MultiTenantConnectionProvider {

    @Autowired
    private CurrentTenantIdentifierResolver currentTenantIdentifierResolver;
    @Autowired
    private DataSource dataSource;
    @Value("${tenant-statement:SET SCHEMA %s}")
    private String schemaStatement;

    @Override
    public Connection getAnyConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public Connection getConnection(String tenantIdentifie) throws SQLException {
        final Connection connection = getAnyConnection();
        String tenantIdentifier = currentTenantIdentifierResolver.resolveCurrentTenantIdentifier();
        try {
            connection.createStatement().execute(getTenantStatement(tenantIdentifier));
        } catch (SQLException e) {
            throw new HibernateException("Problem setting schema to " + tenantIdentifier, e);
        }
        return connection;
    }

    private String getTenantStatement(String tenantIdentifier) {
        return String.format(schemaStatement, tenantIdentifier);
    }

    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        try (connection) {
            connection.createStatement().execute(getTenantStatement(null));
        } catch (SQLException e) {
            throw new HibernateException("Problem setting schema to " + tenantIdentifier, e);
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean isUnwrappableAs(Class unwrapType) {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        return null;
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return true;
    }
}

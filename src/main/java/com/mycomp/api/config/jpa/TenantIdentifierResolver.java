package com.mycomp.api.config.jpa;

import com.mycomp.api.config.TenantContext;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author Daniel
 */
@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver {

    @Value("${default-tenant-id:public}")
    private String defaultTenantId;

    @Override
    public String resolveCurrentTenantIdentifier() {
        String tenantId = TenantContext.getCurrentTenant();
        if (tenantId != null) {
            return tenantId;
        }
        return defaultTenantId;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}

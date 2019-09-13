package com.mycomp.api.tenant.service;

import com.mycomp.api.config.liquibase.TenantDatabaseService;
import com.mycomp.api.tenant.model.jpa.Tenant;
import com.mycomp.api.tenant.repository.TenantRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Daniel
 */
@Service
public class TenantService extends TenantDatabaseService {

    @Autowired
    private TenantRepository tenantRepository;

    @Override
    public List<String> getTenants() {
        return ((List<Tenant>) tenantRepository.findAll()).stream().map(Tenant::getName).collect(Collectors.toList());
    }

}

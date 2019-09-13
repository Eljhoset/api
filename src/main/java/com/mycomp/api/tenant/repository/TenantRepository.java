package com.mycomp.api.tenant.repository;

import com.mycomp.api.tenant.model.jpa.Tenant;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 *
 * @author Daniel
 */
@RepositoryRestResource(exported = false)
public interface TenantRepository extends PagingAndSortingRepository<Tenant, Long> {
}

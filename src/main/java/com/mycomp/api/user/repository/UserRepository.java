package com.mycomp.api.user.repository;

import com.mycomp.api.user.model.jpa.Users;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 *
 * @author Daniel
 */
@RepositoryRestResource(exported = false)
public interface UserRepository extends PagingAndSortingRepository<Users, String> {

}

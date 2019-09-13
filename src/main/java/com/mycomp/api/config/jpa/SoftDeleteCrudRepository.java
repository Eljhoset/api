package com.mycomp.api.config.jpa;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.dsl.Expressions;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Daniel
 * @param <T>
 * @param <ID>
 * @param <Q>
 */
@NoRepositoryBean
public interface SoftDeleteCrudRepository<T extends BaseEntity, Q extends EntityPath<?>, ID extends Long> extends PagingAndSortingRepository<T, ID>,
        JpaSpecificationExecutor<T>, QuerydslPredicateExecutor<T>, QuerydslBinderCustomizer<Q> {

    @Override
    @Query("update #{#entityName} e set e.status='DELETE', e.deletedBy=?#{principal}, e.deletedAt=CURRENT_TIMESTAMP where e.id = ?1")
    @Transactional
    @Modifying
    void deleteById(Long id);

    @Override
    @Transactional
    default void delete(T entity) {
        deleteById(entity.getId());
    }

    @Override
    @Transactional
    default void deleteAll(Iterable<? extends T> entities) {
        entities.forEach(entitiy -> deleteById(entitiy.getId()));
    }

    @Override
    @Query("update #{#entityName} e set e.status='DELETE',e.deletedBy=?#{principal}, e.deletedAt=CURRENT_TIMESTAM")
    @Transactional
    @Modifying
    void deleteAll();

    @Override
    @Transactional(readOnly = true)
    @Query("select e from #{#entityName} e where e.id = ?1 and e.status!='DELETE'")
    Optional<T> findById(ID id);

    @Query("select e from #{#entityName} e where e.status = 'DELETE'")
    @Transactional(readOnly = true)
    Page<T> findInactive(Pageable pageable);

    @Override
    @RestResource(exported = false)
    @Transactional(readOnly = true)
    @Query("select e from #{#entityName} e where e.status!='DELETE'")
    Iterable<T> findAll();

    @Override
    @Transactional(readOnly = true)
    @Query("select e from #{#entityName} e where e.id in ?1 and e.status!='DELETE'")
    Iterable<T> findAllById(Iterable<ID> itrbl);

    @Override
    @Transactional(readOnly = true)
    @Query("select e from #{#entityName} e where e.status!='DELETE'")
    public Page<T> findAll(Pageable pgbl);

    @Override
    @RestResource(exported = false)
    @Transactional(readOnly = true)
    @Query("select e from #{#entityName} e where e.status!='DELETE'")
    public Iterable<T> findAll(Sort sort);

    @Override
    @Transactional(readOnly = true)
    @Query("select count(e) from #{#entityName} e where e.status!='DELETE'")
    long count();

    @Override
    default void customize(QuerydslBindings bindings, Q q) {
        bindings.excluding(Expressions.path(EntityStatus.class, PathMetadataFactory.forProperty(q, "status")));
    }

}

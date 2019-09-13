package com.mycomp.api.model.repository;

import com.mycomp.api.config.jpa.SoftDeleteCrudRepository;
import com.mycomp.api.model.model.jpa.Model;
import com.mycomp.api.model.model.jpa.QModel;

/**
 *
 * @author Daniel
 */
public interface ModelRepository extends SoftDeleteCrudRepository<Model, QModel, Long> {

}

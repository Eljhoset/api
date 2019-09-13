package com.mycomp.api.tenant.model.jpa;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Daniel
 */
@Data
@Entity
@Table(schema = "public")
public class Tenant implements Serializable {

    @Id
    private String name;
}

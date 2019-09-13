package com.mycomp.api.model.model.jpa;

import com.mycomp.api.config.jpa.BaseEntity;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Where;

/**
 *
 * @author Daniel
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = false)
public class Model extends BaseEntity implements Serializable {

    @NotBlank
    private String name;
}

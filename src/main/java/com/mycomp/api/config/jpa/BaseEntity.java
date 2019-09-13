package com.mycomp.api.config.jpa;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 *
 * @author Daniel
 */
@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties({"createdBy", "createdAt", "updatedBy", "updatedAt", "deletedBy", "deletedAt"})
public class BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @CreatedBy
    private String createdBy;
    @CreationTimestamp
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date createdAt;
    @LastModifiedBy
    private String updatedBy;
    @UpdateTimestamp
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date updatedAt;
    @Enumerated(EnumType.STRING)
    private EntityStatus status;
    private String deletedBy;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date deletedAt;

    @PrePersist
    protected void prePersist() {
        status = EntityStatus.ACTIVE;
    }

}

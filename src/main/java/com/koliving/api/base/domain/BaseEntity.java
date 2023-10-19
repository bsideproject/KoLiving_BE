package com.koliving.api.base.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static java.lang.Boolean.TRUE;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    private boolean deleted = Boolean.FALSE;

    @CreatedDate
    @Column(nullable = false, name = "CREATED_AT")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false, name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    public void delete() {
        this.deleted = TRUE;
    }
}

package com.koliving.api.email;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@NoArgsConstructor
@Entity
public class ConfirmationToken {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Integer id;
    private String token;
    private String email;

    @CreationTimestamp
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    @Transient
    @Value("${spring.mail.properties.mail.auth.validity-period:30}")
    private long validityPeriod;
    private boolean isResended;
    private boolean isConfirmed;

    @Builder
    public ConfirmationToken(String email) {
        this.token = UUID.randomUUID().toString();
        this.email = email;
        this.expiresAt = LocalDateTime.now().plusMinutes(validityPeriod);
        this.isResended = false;
        this.isConfirmed = false;
    }

    public void confirmed() {
        this.isConfirmed = true;
    }
}

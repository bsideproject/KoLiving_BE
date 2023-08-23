package com.koliving.api.token.confirmation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "TB_CONFIRMATION_TOKEN")
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConfirmationToken {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String token;
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "TOKEN_TYPE")
    private ConfirmationTokenType tokenType;

    @CreationTimestamp
    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate;

    @Column(name = "EXPIRED_DATE")
    private LocalDateTime expiredDate;

    @Column(name = "B_RESENDED")
    private boolean bResended;

    @Column(name = "B_CONFIRMED")
    private boolean bConfirmed;

    @Builder
    public ConfirmationToken(String email, ConfirmationTokenType tokenType, long validityPeriod) {
        this.email = email;
        this.token = UUID.randomUUID().toString();
        this.tokenType = tokenType;
        this.expiredDate = LocalDateTime.now().plusMinutes(validityPeriod);
    }

    public void confirm() {
        this.bConfirmed = true;
    }
}

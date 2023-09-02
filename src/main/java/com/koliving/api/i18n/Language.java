package com.koliving.api.i18n;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name = "TB_LANGUAGE")
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"LOCALE", "MESSAGE_KEY"})
})
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Language {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(name = "LOCALE", length = 20)
    private String locale;

    @Column(name = "MESSAGE_KEY", length = 210)
    private String messageKey;

    @Column(name = "MESSAGE_PATTERN")
    private String messagePattern;
}

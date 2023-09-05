package com.koliving.api.i18n;

import com.koliving.api.base.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity(name = "TB_LANGUAGE")
@Getter
@ToString
@SQLDelete(sql = "UPDATE TB_LANGUAGE SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
@EqualsAndHashCode(of = "id", callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Language extends BaseEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String locale;

    @Column(name = "MESSAGE_KEY")
    private String messageKey;

    @Column(name = "MESSAGE_PATTERN")
    private String messagePattern;

    private Language(String locale, String messageKey, String messagePattern) {
        this.locale = locale;
        this.messageKey = messageKey;
        this.messagePattern = messagePattern;
    }

    public static Language valueOf(String locale, String messageKey,String messagePattern) {
        return new Language(locale, messageKey, messagePattern);
    }
}

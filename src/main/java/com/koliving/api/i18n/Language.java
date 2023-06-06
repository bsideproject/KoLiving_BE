package com.koliving.api.i18n;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

@Getter
@Entity
public class Language {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Integer id;
    private String locale;
    private String messageKey;
    private String messageContent;

}

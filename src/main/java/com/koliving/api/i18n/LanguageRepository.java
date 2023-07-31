package com.koliving.api.i18n;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface LanguageRepository extends JpaRepository<Language, Long> {

    Optional<Language> findByLocaleAndMessageKey(String locale, String key);
}

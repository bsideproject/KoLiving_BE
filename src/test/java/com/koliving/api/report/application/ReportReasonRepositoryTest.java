package com.koliving.api.report.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.koliving.api.report.domain.ReportReason;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * author : haedoang date : 2023/11/29 description :
 */
@DataJpaTest
class ReportReasonRepositoryTest {
    @Autowired
    private ReportReasonRepository reportReasonRepository;

    @Test
    @DisplayName("리포트 사유 생성하기")
    void create() {
        // given
        final ReportReason given = ReportReason.of("Not a Real Place");

        // when
        final ReportReason actual = reportReasonRepository.save(given);

        // then
        assertThat(actual.getId()).isNotNull();
    }
}
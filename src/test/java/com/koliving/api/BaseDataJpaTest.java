package com.koliving.api;

import com.koliving.api.config.QueryDslTestConfig;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DisplayName("DataJpaTest  시 import")
@Import(QueryDslTestConfig.class)
@DataJpaTest
public class BaseDataJpaTest {

}

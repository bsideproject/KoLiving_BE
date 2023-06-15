package com.koliving.api.clock;

import java.time.LocalDateTime;

public interface IClock {
    default LocalDateTime now() {
        return LocalDateTime.now();
    }
}

package com.koliving.api.location.infra;

import com.koliving.api.location.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;


public interface LocationRepository extends JpaRepository<Location, Long> {
}

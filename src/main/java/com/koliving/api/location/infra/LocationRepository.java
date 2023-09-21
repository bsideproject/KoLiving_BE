package com.koliving.api.location.infra;

import com.koliving.api.location.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByName(String name);
}

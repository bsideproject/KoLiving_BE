package com.koliving.api.room.infra;

import com.koliving.api.room.domain.Furnishing;
import com.koliving.api.room.domain.FurnishingType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FurnishingRepository extends JpaRepository<Furnishing, Long> {
    Optional<Furnishing> findByType(FurnishingType type);
}

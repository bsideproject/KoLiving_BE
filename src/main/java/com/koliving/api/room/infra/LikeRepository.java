package com.koliving.api.room.infra;

import com.koliving.api.room.domain.Like;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * author : haedoang date : 2023/10/19 description :
 */
public interface LikeRepository extends JpaRepository<Like, Long> {
}

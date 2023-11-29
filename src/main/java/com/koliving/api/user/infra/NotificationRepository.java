package com.koliving.api.user.infra;

import com.koliving.api.user.domain.Notification;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * author : haedoang date : 2023/11/05 description :
 */
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("select n from TB_NOTIFICATION n where n.sender.id = :senderId and n.createdAt >= :datetime")
    List<Notification> findBySenderId(@Param("senderId") Long senderId, @Param("datetime") LocalDateTime datetime);

    @Query("select n from TB_NOTIFICATION n where n.receiver.id = :receiverId and n.createdAt >= :datetime")
    List<Notification> findByReceiverId(@Param("receiverId") Long receiverId, @Param("datetime") LocalDateTime datetime);

    @Query("select n from TB_NOTIFICATION n where n.receiver.id = :receiverId and n.createdAt >= :datetime and n.confirm=false")
    List<Notification> findNotConfirmedByReceiverId(@Param("receiverId") Long receiverId, @Param("datetime") LocalDateTime datetime);

}

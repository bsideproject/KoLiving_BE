package com.koliving.api.user.infra;

import com.koliving.api.user.domain.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * author : haedoang date : 2023/11/05 description :
 */
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllBySenderId(Long senderId);

    List<Notification> findAllByReceiverId(Long senderId);

}

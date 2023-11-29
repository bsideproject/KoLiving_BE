package com.koliving.api.user.domain;

import static com.koliving.api.base.ServiceError.FORBIDDEN;

import com.koliving.api.base.domain.BaseEntity;
import com.koliving.api.base.exception.KolivingServiceException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * author : haedoang date : 2023/11/05 description :
 */
@Entity(name = "TB_NOTIFICATION")
@DynamicInsert
@DynamicUpdate
@Getter
@ToString
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "SENDER_ID")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "RECEIVER_ID")
    private User receiver;

    @Column
    private Boolean confirm = Boolean.FALSE;

    private Notification(User sender, User receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }

    public void confirm(User user) {
        if (!receiver.equals(user)) {
            throw new KolivingServiceException(FORBIDDEN);
        }
        this.confirm = Boolean.TRUE;
    }

    public static Notification of(User sender, User receiver) {
        return new Notification(sender, receiver);
    }
}

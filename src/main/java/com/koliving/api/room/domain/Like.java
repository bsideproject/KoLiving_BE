package com.koliving.api.room.domain;

import static lombok.AccessLevel.PROTECTED;

import com.koliving.api.base.domain.BaseEntity;
import com.koliving.api.user.domain.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

/**
 * author : haedoang date : 2023/10/19 description :
 */
@Getter
@Entity(name = "TB_ROOM_LIKE")
@SQLDelete(sql = "UPDATE TB_ROOM_LIKE SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = PROTECTED)
public class Like extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Like(Room room, User user) {
        this.room = room;
        this.user = user;
    }

    public static Like of(Room room, User user) {
        return new Like(room, user);
    }
}

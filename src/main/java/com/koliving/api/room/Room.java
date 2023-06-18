package com.koliving.api.room;

import com.koliving.api.comment.Comment;
import com.koliving.api.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Room {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column(name = "room_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User User;

    @OneToMany(mappedBy = "room")
    private List<Comment> comment = new ArrayList<>();

    private String lcation;

    private Long monthlyFee;

    private Long deposit;

    private Long maintenanceFee;

    private String availableDate;

    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    //TODO: furnishing 옵션정보는 어떻게?, Room info 도 매핑 테이블 따로 만들어서 해야 하나?? (침실, 욕실, 룸메 따로 하면편한데)
    private String furnishing;

    private String roomInfo;

    private Integer bedRoom;

    private Integer bathRoom;

    private Integer roomMate;

    private String roomContent;

    private String roomImage;

    @CreationTimestamp
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime lastModifiedDate;
}

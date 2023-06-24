package com.koliving.api.room;

import com.koliving.api.furnishing.Furnishing;
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

    private String lcation;

    private Long monthlyFee;

    private Long deposit;

    private Long maintenanceFee;

    private String availableDate;

    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    @OneToMany(mappedBy = "furnishing")
    private List<Furnishing> furnishing = new ArrayList<>();

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

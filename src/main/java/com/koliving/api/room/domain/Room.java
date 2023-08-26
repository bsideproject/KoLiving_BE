package com.koliving.api.room.domain;

import com.koliving.api.location.domain.Location;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.EnumType.*;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

//TODO Room 도메인생성

@Getter
@Entity(name = "TB_ROOM")
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = PROTECTED)
public class Room {
    @Transient
    public static final Long MAX_DEPOSIT = 500_000_000L;

    @Transient
    public static final Long MAX_MONTHLY_RENT = 20_000_000L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "location_id")
    private Location location;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "deposit", columnDefinition = "integer default 0"))
    private Money deposit;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "monthly_rent", columnDefinition = "integer default 0"))
    private Money monthlyRent;

    @Embedded
    @AttributeOverrides(
        @AttributeOverride(name = "amount", column = @Column(name = "maintenance_fee", columnDefinition = "integer default 0"))
    )
    private Maintenance maintenance;

    @Enumerated(STRING)
    @Column(name = "room_type", nullable = false)
    private RoomType roomType;

    private Room(Location location, Money deposit, Money monthlyRent, Maintenance maintenance) {
        this.location = location;
        this.deposit = deposit;
        this.monthlyRent = monthlyRent;

    }
//
//    public static Room valueOf(Location location, Money deposit, Money monthlyRent, RoomType roomType, Maintenance maintenance) {
//        return new Room(location, deposit, monthlyRent, roomType, maintenance);
//    }

    //TODO 이미지
    @Transient
    private List<Long> images;

    //TODO 유저
    @Transient
    private Long userId;

}

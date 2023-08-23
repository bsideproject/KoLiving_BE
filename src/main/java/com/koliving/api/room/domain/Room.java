package com.koliving.api.room.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;
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

    @Column(name = "location_id")
    private Long locationId;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "deposit"))
    })
    private Money deposit;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "montly_rent"))
    })
    private Money monthlyRent;

    @OneToMany(orphanRemoval = true)
    @JoinTable(
        name = "TB_ROOM_TYPE_OF_HOUSINGS",
        joinColumns = @JoinColumn(name = "room_id"),
        inverseJoinColumns = @JoinColumn(name = "type_of_housing_id")
    )
    private List<TypeOfHousing> types = new ArrayList<>();

    public Room(Long locationId, Money deposit, Money monthlyRent, List<TypeOfHousing> types) {
        this.locationId = locationId;
        this.deposit = deposit;
        this.monthlyRent = monthlyRent;
        this.types = types;
    }

    //TODO 이미지
    @Transient
    private List<Long> images;

    //TODO 유저
    @Transient
    private Long userId;

}

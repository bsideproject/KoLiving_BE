package com.koliving.api.room.domain;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.koliving.api.base.domain.BaseEntity;
import com.koliving.api.location.domain.Location;
import com.koliving.api.room.domain.info.RoomInfo;
import jakarta.persistence.AttributeOverride;
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
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;


@Getter
@Entity(name = "TB_ROOM")
@SQLDelete(sql = "UPDATE TB_ROOM SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = PROTECTED)
public class Room extends BaseEntity {

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
    private Maintenance maintenance;

    @Embedded
    private RoomInfo roomInfo;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
        name = "TB_ROOM_FURNISHINGS",
        joinColumns = @JoinColumn(name = "room_id"),
        inverseJoinColumns = @JoinColumn(name = "furnishing_id")
    )
    private Set<Furnishing> furnishings = new HashSet<>();

    @Column(name = "available_date", nullable = false)
    private LocalDate availableDate;

    @Lob
    private String description;

    private Room(Location location, RoomInfo roomInfo, Money deposit, Money monthlyRent, Maintenance maintenance, Set<Furnishing> furnishings, LocalDate availableDate, String description) {
        this.location = location;
        this.roomInfo = roomInfo;
        this.deposit = deposit;
        this.monthlyRent = monthlyRent;
        this.maintenance = maintenance;
        this.furnishings = furnishings;
        this.availableDate = availableDate;
        this.description = description;
    }

    public static Room valueOf(
        Location location,
        RoomInfo info,
        Money deposit,
        Money monthlyRent,
        Maintenance maintenance,
        Set<Furnishing> furnishings,
        LocalDate availableDate,
        String description
    ) {
        return new Room(location, info, deposit, monthlyRent, maintenance, furnishings, availableDate, description);
    }

    //TODO 이미지
    @Transient
    private List<Long> images;

    //TODO 유저
    @Transient
    private Long userId;

}
